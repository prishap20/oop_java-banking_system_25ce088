package com.bank.exception;

/**
 * Thrown when a current account exceeds its allowable overdraft limit.
 */
public class OverdraftExceededException extends BankingException {
    private final double requestedWithdrawal;
    private final double maxAllowable;

    public OverdraftExceededException(double requestedWithdrawal, double maxAllowable) {
        super(String.format("Overdraft limit breached! Withdrawal of ₹%.2f exceeds allowable limit of ₹%.2f (Balance + Overdraft).",
              requestedWithdrawal, maxAllowable));
        this.requestedWithdrawal = requestedWithdrawal;
        this.maxAllowable = maxAllowable;
    }

    public double getRequestedWithdrawal() {
        return requestedWithdrawal;
    }

    public double getMaxAllowable() {
        return maxAllowable;
    }
}
