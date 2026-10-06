package com.finance.util;

import java.math.BigDecimal;
import java.util.regex.Pattern;

/**
 * Utility class providing form input validation methods across servlets and services.
 * Helps prevent SQL injection, invalid data types, and constraint violations.
 */
public class ValidationUtil {

    private static final Pattern EMAIL_PATTERN = 
            Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    /**
     * Checks if a string is null or empty after trimming whitespace.
     */
    public static boolean isEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }

    /**
     * Validates email format and maximum database length (50 chars).
     */
    public static boolean isValidEmail(String email) {
        if (isEmpty(email) || email.length() > 50) {
            return false;
        }
        return EMAIL_PATTERN.matcher(email.trim()).matches();
    }

    /**
     * Validates password length according to database schema restriction (VARCHAR(16)).
     * Must be between 6 and 16 characters long.
     */
    public static boolean isValidPassword(String password) {
        if (isEmpty(password)) {
            return false;
        }
        int len = password.trim().length();
        return len >= 6 && len <= 16;
    }

    /**
     * Validates if string can be parsed into a positive BigDecimal amount.
     */
    public static boolean isPositiveAmount(String amountStr) {
        if (isEmpty(amountStr)) {
            return false;
        }
        try {
            BigDecimal amount = new BigDecimal(amountStr.trim());
            return amount.compareTo(BigDecimal.ZERO) > 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
