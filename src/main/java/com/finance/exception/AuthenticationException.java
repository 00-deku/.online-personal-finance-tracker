package com.finance.exception;

/**
 * Exception thrown when authentication fails (invalid credentials, unknown user).
 */
public class AuthenticationException extends FinanceTrackerException {

    public AuthenticationException(String message) {
        super(message);
    }
}
