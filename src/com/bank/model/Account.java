package com.bank.model;

import com.bank.exception.AuthenticationException;
import com.bank.exception.InsufficientFundsException;
import com.bank.exception.InvalidAmountException;
import com.bank.exception.OverdraftExceededException;
import com.bank.interfaces.Reportable;
import com.bank.interfaces.Transactable;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Abstract Base Class representing a general Bank Account.
 * Demonstrates:
 *  - Abstraction: Abstract class with abstract and concrete operations
 *  - Encapsulation: Private state with guarded accessors/mutators
 *  - Polymorphism: Base contract overridden by specialized account types
 */
public abstract class Account implements Transactable, Reportable, Serializable {
    private static final long serialVersionUID = 1L;
    private static final int MAX_FAILED_ATTEMPTS = 3;

    // Encapsulated attributes
    private final String accountNumber;
    private final String customerId;
    protected double balance;
    private String pin;
    private int failedAttempts;
    private boolean isLocked;
    private final LocalDateTime createdAt;
    private final List<Transaction> transactions;

    /**
     * Base constructor for account hierarchy.
     */
    public Account(String accountNumber, String customerId, double initialDeposit, String pin) 
            throws InvalidAmountException {
        if (initialDeposit < 0) {
            throw new InvalidAmountException("Initial deposit cannot be negative.");
        }
        if (pin == null || pin.trim().length() != 4 || !pin.chars().allMatch(Character::isDigit)) {
            throw new IllegalArgumentException("PIN must be an exact 4-digit numeric code.");
        }

        this.accountNumber = accountNumber;
        this.customerId = customerId;
        this.balance = initialDeposit;
        this.pin = pin;
        this.failedAttempts = 0;
        this.isLocked = false;
        this.createdAt = LocalDateTime.now();
        this.transactions = new ArrayList<>();

        // Record account opening deposit transaction
        if (initialDeposit > 0) {
            recordTransaction(TransactionType.DEPOSIT, initialDeposit, "Initial Account Opening Deposit", null);
        }
    }

    // Abstract methods enforced on derived classes (Polymorphism)
    public abstract String getAccountType();
    public abstract double calculateInterest();
    public abstract double getMinimumBalance();

    // Security & Authentication methods
    public boolean verifyPin(String enteredPin) throws AuthenticationException {
        if (isLocked) {
            throw new AuthenticationException("Account " + accountNumber + " is LOCKED due to excessive failed attempts. Please contact bank admin.");
        }
        if (this.pin.equals(enteredPin)) {
            this.failedAttempts = 0;
            return true;
        } else {
            this.failedAttempts++;
            int remaining = MAX_FAILED_ATTEMPTS - failedAttempts;
            if (this.failedAttempts >= MAX_FAILED_ATTEMPTS) {
                this.isLocked = true;
                throw new AuthenticationException("PIN incorrect! Account has now been LOCKED for security reasons.");
            }
            throw new AuthenticationException(String.format("Invalid PIN! Attempts remaining: %d", remaining));
        }
    }

    public void changePin(String currentPin, String newPin) throws AuthenticationException {
        verifyPin(currentPin);
        if (newPin == null || newPin.trim().length() != 4 || !newPin.chars().allMatch(Character::isDigit)) {
            throw new IllegalArgumentException("New PIN must be an exact 4-digit numeric code.");
        }
        this.pin = newPin;
    }

    public void unlockAccount() {
        this.isLocked = false;
        this.failedAttempts = 0;
    }

    // Transactable implementation
    @Override
    public synchronized void deposit(double amount, String remarks) throws InvalidAmountException {
        if (amount <= 0) {
            throw new InvalidAmountException(amount);
        }
        this.balance += amount;
        recordTransaction(TransactionType.DEPOSIT, amount, remarks, null);
    }

    // Overloaded method (Compile-time Polymorphism)
    public void deposit(double amount) throws InvalidAmountException {
        deposit(amount, "Standard Cash Deposit");
    }

    @Override
    public abstract void withdraw(double amount, String remarks) 
            throws InsufficientFundsException, InvalidAmountException, OverdraftExceededException;

    // Overloaded method
    public void withdraw(double amount) 
            throws InsufficientFundsException, InvalidAmountException, OverdraftExceededException {
        withdraw(amount, "Standard Cash Withdrawal");
    }

    @Override
    public synchronized void transfer(Account toAccount, double amount, String remarks) 
            throws InsufficientFundsException, InvalidAmountException, OverdraftExceededException {
        if (toAccount == null) {
            throw new IllegalArgumentException("Destination account cannot be null.");
        }
        if (this.accountNumber.equals(toAccount.getAccountNumber())) {
            throw new IllegalArgumentException("Cannot transfer funds to the same account.");
        }
        if (amount <= 0) {
            throw new InvalidAmountException(amount);
        }

        // Withdraw from source account (validates sufficient funds & overdraft)
        this.withdraw(amount, "Fund Transfer to " + toAccount.getAccountNumber() + (remarks != null ? " - " + remarks : ""));
        // Deposit into destination account
        toAccount.deposit(amount, "Fund Transfer from " + this.accountNumber + (remarks != null ? " - " + remarks : ""));
    }

    // Helper to log ledger entries
    protected synchronized void recordTransaction(TransactionType type, double amount, String remarks, String relatedAccount) {
        Transaction tx = new Transaction(type, amount, this.balance, remarks, relatedAccount);
        this.transactions.add(tx);
    }

    // Reportable implementation
    @Override
    public String generateStatement() {
        StringBuilder sb = new StringBuilder();
        sb.append("========================================================================================\n");
        sb.append(String.format("                     ACCOUNT STATEMENT - %s\n", getAccountType().toUpperCase()));
        sb.append("========================================================================================\n");
        sb.append(String.format("Account Number : %s\n", accountNumber));
        sb.append(String.format("Customer ID    : %s\n", customerId));
        sb.append(String.format("Account Type   : %s\n", getAccountType()));
        sb.append(String.format("Created On     : %s\n", createdAt.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))));
        sb.append(String.format("Current Balance: ₹%.2f\n", balance));
        sb.append(String.format("Status         : %s\n", isLocked ? "LOCKED" : "ACTIVE"));
        sb.append("----------------------------------------------------------------------------------------\n");
        sb.append(String.format("%-20s | %-12s | %-24s | %-12s | %-12s\n", "Date & Time", "Txn ID", "Type / Description", "Amount", "Balance After"));
        sb.append("----------------------------------------------------------------------------------------\n");

        if (transactions.isEmpty()) {
            sb.append("  (No transactions recorded yet)\n");
        } else {
            for (Transaction tx : transactions) {
                sb.append(String.format("%-20s | %-12s | %-24s | %s₹%-10.2f | ₹%-10.2f\n",
                        tx.getFormattedTimestamp(),
                        tx.getTransactionId(),
                        tx.getType().getDescription(),
                        tx.getType().getSymbol(),
                        tx.getAmount(),
                        tx.getBalanceAfter()));
            }
        }
        sb.append("========================================================================================\n");
        return sb.toString();
    }

    @Override
    public String getAccountSummary() {
        return String.format("[%s] Acc: %s | Customer: %s | Balance: ₹%.2f | Status: %s",
                getAccountType(), accountNumber, customerId, balance, isLocked ? "LOCKED" : "ACTIVE");
    }

    @Override
    public void exportStatementToFile(String targetFilePath) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(targetFilePath))) {
            writer.write(generateStatement());
        }
    }

    // Getters
    public String getAccountNumber() {
        return accountNumber;
    }

    public String getCustomerId() {
        return customerId;
    }

    public double getBalance() {
        return balance;
    }

    public boolean isLocked() {
        return isLocked;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public List<Transaction> getTransactions() {
        return Collections.unmodifiableList(transactions);
    }

    @Override
    public String toString() {
        return getAccountSummary();
    }
}
