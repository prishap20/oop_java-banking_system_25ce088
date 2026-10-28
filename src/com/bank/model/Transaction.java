package com.bank.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * Immutable transaction record representing an entry in the account ledger.
 * Demonstrates Encapsulation.
 */
public class Transaction implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final String transactionId;
    private final LocalDateTime timestamp;
    private final TransactionType type;
    private final double amount;
    private final double balanceAfter;
    private final String remarks;
    private final String relatedAccount;

    public Transaction(TransactionType type, double amount, double balanceAfter, String remarks, String relatedAccount) {
        this.transactionId = "TXN" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        this.timestamp = LocalDateTime.now();
        this.type = type;
        this.amount = amount;
        this.balanceAfter = balanceAfter;
        this.remarks = (remarks != null && !remarks.trim().isEmpty()) ? remarks.trim() : "-";
        this.relatedAccount = (relatedAccount != null && !relatedAccount.trim().isEmpty()) ? relatedAccount.trim() : "-";
    }

    public String getTransactionId() {
        return transactionId;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public String getFormattedTimestamp() {
        return timestamp.format(FORMATTER);
    }

    public TransactionType getType() {
        return type;
    }

    public double getAmount() {
        return amount;
    }

    public double getBalanceAfter() {
        return balanceAfter;
    }

    public String getRemarks() {
        return remarks;
    }

    public String getRelatedAccount() {
        return relatedAccount;
    }

    @Override
    public String toString() {
        return String.format("[%s] %-12s | %-16s | %s₹%10.2f | Balance: ₹%10.2f | %s (Ref: %s)",
                getFormattedTimestamp(),
                transactionId,
                type.getDescription(),
                type.getSymbol(),
                amount,
                balanceAfter,
                remarks,
                relatedAccount);
    }
}
