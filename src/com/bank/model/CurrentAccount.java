package com.bank.model;

import com.bank.exception.InvalidAmountException;
import com.bank.exception.OverdraftExceededException;

/**
 * Current Account specialization with Overdraft facility.
 * Demonstrates:
 *  - Inheritance: extends Account
 *  - Polymorphism: custom withdraw logic permitting overdraft
 */
public class CurrentAccount extends Account {
    private static final long serialVersionUID = 1L;

    public static final double DEFAULT_OVERDRAFT_LIMIT = 25000.0;
    public static final double DEFAULT_OVERDRAFT_FEE_PERCENT = 1.0;

    private final double overdraftLimit;
    private final double overdraftFeePercent;

    public CurrentAccount(String accountNumber, String customerId, double initialDeposit, String pin) 
            throws InvalidAmountException {
        this(accountNumber, customerId, initialDeposit, pin, DEFAULT_OVERDRAFT_LIMIT, DEFAULT_OVERDRAFT_FEE_PERCENT);
    }

    public CurrentAccount(String accountNumber, String customerId, double initialDeposit, String pin, 
                          double overdraftLimit, double overdraftFeePercent) 
            throws InvalidAmountException {
        super(accountNumber, customerId, initialDeposit, pin);
        this.overdraftLimit = overdraftLimit;
        this.overdraftFeePercent = overdraftFeePercent;
    }

    @Override
    public String getAccountType() {
        return "Current Account";
    }

    @Override
    public double getMinimumBalance() {
        return 0.0; // Current accounts typically have no minimum balance constraint
    }

    public double getOverdraftLimit() {
        return overdraftLimit;
    }

    public double getAvailableFunds() {
        return balance + overdraftLimit;
    }

    @Override
    public synchronized void withdraw(double amount, String remarks) 
            throws InvalidAmountException, OverdraftExceededException {
        if (amount <= 0) {
            throw new InvalidAmountException(amount);
        }

        double maxAllowable = balance + overdraftLimit;
        if (amount > maxAllowable) {
            throw new OverdraftExceededException(amount, maxAllowable);
        }

        this.balance -= amount;
        recordTransaction(TransactionType.WITHDRAWAL, amount, remarks, null);

        // If account entered overdraft, apply small overdraft fee
        if (this.balance < 0) {
            double overdrawnAmount = Math.abs(this.balance);
            double fee = (overdrawnAmount * overdraftFeePercent) / 100.0;
            if (fee > 0) {
                this.balance -= fee;
                recordTransaction(TransactionType.FEE_DEBIT, fee, 
                                  String.format("Overdraft Fee (%.1f%% on ₹%.2f overdrawn)", overdraftFeePercent, overdrawnAmount), null);
            }
        }
    }

    @Override
    public double calculateInterest() {
        // Current accounts do not earn interest
        return 0.0;
    }

    @Override
    public String getAccountSummary() {
        return super.getAccountSummary() + String.format(" | Overdraft Limit: ₹%.2f | Total Available: ₹%.2f", 
                                                         overdraftLimit, getAvailableFunds());
    }
}
