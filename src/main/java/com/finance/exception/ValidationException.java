package com.finance.exception;

/**
 * Custom exception thrown when input validation fails (invalid email, bad amounts, missing fields).
 */
public class ValidationException extends FinanceTrackerException {

    public ValidationException(String message) {
        super(message);
    }
}
