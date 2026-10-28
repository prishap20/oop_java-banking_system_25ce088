package com.bank.exception;

/**
 * Thrown when an account number cannot be found in the banking system.
 */
public class AccountNotFoundException extends BankingException {
    private final String accountNumber;

    public AccountNotFoundException(String accountNumber) {
        super(String.format("Account '%s' does not exist in the bank registry.", accountNumber));
        this.accountNumber = accountNumber;
    }

    public String getAccountNumber() {
        return accountNumber;
    }
}
