package com.finance.exception;

/**
 * Base custom application exception for the Personal Finance Tracker application.
 */
public class FinanceTrackerException extends Exception {

    public FinanceTrackerException(String message) {
        super(message);
    }

    public FinanceTrackerException(String message, Throwable cause) {
        super(message, cause);
    }
}
