package com.bank.model;

import com.bank.exception.InsufficientFundsException;
import com.bank.exception.InvalidAmountException;

/**
 * Savings Account specialization.
 * Demonstrates:
 *  - Inheritance: extends Account
 *  - Polymorphism: overrides withdraw() and calculateInterest()
 */
public class SavingsAccount extends Account {
    private static final long serialVersionUID = 1L;
    
    public static final double DEFAULT_MINIMUM_BALANCE = 1000.0;
    public static final double DEFAULT_INTEREST_RATE = 4.0; // 4% per annum

    private final double minimumBalance;
    private final double annualInterestRate;

    public SavingsAccount(String accountNumber, String customerId, double initialDeposit, String pin) 
            throws InvalidAmountException {
        this(accountNumber, customerId, initialDeposit, pin, DEFAULT_MINIMUM_BALANCE, DEFAULT_INTEREST_RATE);
    }

    public SavingsAccount(String accountNumber, String customerId, double initialDeposit, String pin, 
                          double minimumBalance, double annualInterestRate) 
            throws InvalidAmountException {
        super(accountNumber, customerId, initialDeposit, pin);
        if (initialDeposit < minimumBalance) {
            throw new InvalidAmountException(
                String.format("Initial deposit (₹%.2f) cannot be less than minimum balance requirement (₹%.2f).", 
                              initialDeposit, minimumBalance));
        }
        this.minimumBalance = minimumBalance;
        this.annualInterestRate = annualInterestRate;
    }

    @Override
    public String getAccountType() {
        return "Savings Account";
    }

    @Override
    public double getMinimumBalance() {
        return minimumBalance;
    }

    public double getAnnualInterestRate() {
        return annualInterestRate;
    }

    @Override
    public synchronized void withdraw(double amount, String remarks) 
            throws InsufficientFundsException, InvalidAmountException {
        if (amount <= 0) {
            throw new InvalidAmountException(amount);
        }
        if ((balance - amount) < minimumBalance) {
            throw new InsufficientFundsException(balance - minimumBalance, amount);
        }
        this.balance -= amount;
        recordTransaction(TransactionType.WITHDRAWAL, amount, remarks, null);
    }

    @Override
    public double calculateInterest() {
        // Simple annual interest accrued on current balance
        return (this.balance * this.annualInterestRate) / 100.0;
    }

    /**
     * Credits calculated periodic interest directly to the account balance.
     * @return Interest amount credited
     */
    public synchronized double applyInterest() {
        double interest = calculateInterest() / 12.0; // Monthly installment
        if (interest > 0) {
            this.balance += interest;
            recordTransaction(TransactionType.INTEREST_CREDIT, interest, 
                              String.format("Monthly Interest Credit (@%.2f%% p.a.)", annualInterestRate), null);
        }
        return interest;
    }

    @Override
    public String getAccountSummary() {
        return super.getAccountSummary() + String.format(" | Min Bal: ₹%.2f | Interest: %.2f%%", minimumBalance, annualInterestRate);
    }
}
