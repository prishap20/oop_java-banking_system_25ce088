package com.bank.model;

/**
 * Enumeration representing all types of banking ledger transactions.
 */
public enum TransactionType {
    DEPOSIT("Cash/Cheque Deposit", "+"),
    WITHDRAWAL("Cash Withdrawal", "-"),
    TRANSFER_OUT("Fund Transfer (Sent)", "-"),
    TRANSFER_IN("Fund Transfer (Received)", "+"),
    INTEREST_CREDIT("Interest Payout", "+"),
    PENALTY_DEBIT("Early Withdrawal Penalty", "-"),
    FEE_DEBIT("Service / Overdraft Fee", "-");

    private final String description;
    private final String symbol;

    TransactionType(String description, String symbol) {
        this.description = description;
        this.symbol = symbol;
    }

    public String getDescription() {
        return description;
    }

    public String getSymbol() {
        return symbol;
    }
}
