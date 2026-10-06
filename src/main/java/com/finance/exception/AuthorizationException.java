package com.finance.exception;

/**
 * Exception thrown when a user attempts to access a resource or role restricted page without permission.
 */
public class AuthorizationException extends FinanceTrackerException {

    public AuthorizationException(String message) {
        super(message);
    }
}
