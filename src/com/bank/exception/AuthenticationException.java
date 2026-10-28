package com.bank.exception;

/**
 * Thrown when credentials (such as PIN) fail authentication or account is locked.
 */
public class AuthenticationException extends BankingException {
    public AuthenticationException(String message) {
        super(message);
    }
}
