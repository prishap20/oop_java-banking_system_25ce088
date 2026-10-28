package com.bank.model;

import com.bank.exception.InsufficientFundsException;
import com.bank.exception.InvalidAmountException;

import java.time.LocalDateTime;

/**
 * Fixed Deposit (Term Deposit) Account specialization.
 * Demonstrates:
 *  - Inheritance: extends Account
 *  - Polymorphism: Term-based interest calculation and maturity validation
 */
public class FixedDepositAccount extends Account {
    private static final long serialVersionUID = 1L;

    public static final double DEFAULT_FD_INTEREST_RATE = 7.5; // 7.5% per annum
    public static final double DEFAULT_PREMATURE_PENALTY_PERCENT = 2.0; // 2% penalty for early breaking

    private final double principalAmount;
    private final double annualInterestRate;
    private final int tenureMonths;
    private final LocalDateTime maturityDate;
    private final double prematurePenaltyPercent;
    private boolean isMatured;

    public FixedDepositAccount(String accountNumber, String customerId, double principalAmount, String pin, int tenureMonths) 
            throws InvalidAmountException {
        this(accountNumber, customerId, principalAmount, pin, tenureMonths, DEFAULT_FD_INTEREST_RATE, DEFAULT_PREMATURE_PENALTY_PERCENT);
    }

    public FixedDepositAccount(String accountNumber, String customerId, double principalAmount, String pin, 
                               int tenureMonths, double annualInterestRate, double prematurePenaltyPercent) 
            throws InvalidAmountException {
        super(accountNumber, customerId, principalAmount, pin);
        if (principalAmount < 5000.0) {
            throw new InvalidAmountException("Minimum deposit amount for a Fixed Deposit is ₹5,000.");
        }
        if (tenureMonths < 1) {
            throw new IllegalArgumentException("Tenure must be at least 1 month.");
        }

        this.principalAmount = principalAmount;
        this.annualInterestRate = annualInterestRate;
        this.tenureMonths = tenureMonths;
        this.prematurePenaltyPercent = prematurePenaltyPercent;
        this.maturityDate = getCreatedAt().plusMonths(tenureMonths);
        this.isMatured = false;
    }

    @Override
    public String getAccountType() {
        return "Fixed Deposit";
    }

    @Override
    public double getMinimumBalance() {
        return principalAmount;
    }

    public int getTenureMonths() {
        return tenureMonths;
    }

    public LocalDateTime getMaturityDate() {
        return maturityDate;
    }

    public double getAnnualInterestRate() {
        return annualInterestRate;
    }

    /**
     * Calculates compound maturity amount:
     * A = P * (1 + r / (n * 100))^(n * t)
     * Compounded quarterly (n = 4).
     */
    @Override
    public double calculateInterest() {
        double r = annualInterestRate / 100.0;
        double t = tenureMonths / 12.0;
        int n = 4; // Quarterly compounding
        double maturityAmount = principalAmount * Math.pow(1 + (r / n), n * t);
        return maturityAmount - principalAmount;
    }

    public double getEstimatedMaturityAmount() {
        return principalAmount + calculateInterest();
    }

    public boolean checkMaturity() {
        if (!isMatured && LocalDateTime.now().isAfter(maturityDate)) {
            this.isMatured = true;
        }
        return isMatured;
    }

    @Override
    public synchronized void withdraw(double amount, String remarks) 
            throws InsufficientFundsException, InvalidAmountException {
        if (balance <= 0) {
            throw new InsufficientFundsException(0, amount);
        }

        checkMaturity();

        if (isMatured) {
            // Full payout with compound interest
            double totalPayout = getEstimatedMaturityAmount();
            recordTransaction(TransactionType.INTEREST_CREDIT, calculateInterest(), "FD Maturity Interest Credit", null);
            this.balance = 0;
            recordTransaction(TransactionType.WITHDRAWAL, totalPayout, "Maturity Payout Disbursed", null);
        } else {
            // Premature breaking with penalty
            double penalty = (principalAmount * prematurePenaltyPercent) / 100.0;
            double payout = principalAmount - penalty;
            this.balance = 0;
            recordTransaction(TransactionType.PENALTY_DEBIT, penalty, 
                              String.format("Early Liquidation Penalty (%.1f%%)", prematurePenaltyPercent), null);
            recordTransaction(TransactionType.WITHDRAWAL, payout, 
                              "Premature FD Liquidation Disbursed: " + remarks, null);
        }
    }

    @Override
    public String getAccountSummary() {
        return super.getAccountSummary() + String.format(" | Tenure: %d mos | Rate: %.2f%% | Maturity Value: ₹%.2f",
                tenureMonths, annualInterestRate, getEstimatedMaturityAmount());
    }
}
