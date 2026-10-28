package com.bank.exception;

/**
 * Thrown when an amount provided for deposit, withdrawal, or transfer is invalid (<= 0 or non-numeric).
 */
public class InvalidAmountException extends BankingException {
    private final double amount;

    public InvalidAmountException(double amount) {
        super(String.format("Invalid amount specified: ₹%.2f. Amount must be strictly greater than zero.", amount));
        this.amount = amount;
    }

    public InvalidAmountException(String message) {
        super(message);
        this.amount = 0.0;
    }

    public double getAmount() {
        return amount;
    }
}
