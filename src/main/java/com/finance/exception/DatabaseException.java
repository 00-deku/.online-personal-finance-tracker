package com.finance.exception;

/**
 * Custom exception thrown when database errors (SQLException, connection failures) occur.
 */
public class DatabaseException extends FinanceTrackerException {

    public DatabaseException(String message) {
        super(message);
    }

    public DatabaseException(String message, Throwable cause) {
        super(message, cause);
    }
}
