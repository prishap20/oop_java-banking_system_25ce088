package com.bank.exception;

/**
 * Thrown when an account has insufficient funds to fulfill a withdrawal or transfer.
 */
public class InsufficientFundsException extends BankingException {
    private final double availableBalance;
    private final double requestedAmount;

    public InsufficientFundsException(double availableBalance, double requestedAmount) {
        super(String.format("Insufficient funds! Available balance: ₹%.2f, Requested amount: ₹%.2f", 
              availableBalance, requestedAmount));
        this.availableBalance = availableBalance;
        this.requestedAmount = requestedAmount;
    }

    public double getAvailableBalance() {
        return availableBalance;
    }

    public double getRequestedAmount() {
        return requestedAmount;
    }
}
