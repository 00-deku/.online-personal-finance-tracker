package com.finance.model;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Represents a user category budget spending limit matching the 'BUDGETS' table schema.
 * Includes computed fields 'spent' and 'percentage' for JSP presentation.
 */
public class Budget implements Serializable {

    private static final long serialVersionUID = 1L;

    private String id;
    private String userId;
    private String category;
    private BigDecimal amount;
    private String period;

    // Transient computed fields for UI view
    private BigDecimal spent;
    private double percentage;

    public Budget() {
    }

    public Budget(String id, String userId, String category, BigDecimal amount, String period) {
        this.id = id;
        this.userId = userId;
        this.category = category;
        this.amount = amount;
        this.period = period;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public String getUser_id() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getPeriod() {
        return period;
    }

    public void setPeriod(String period) {
        this.period = period;
    }

    public BigDecimal getSpent() {
        return spent;
    }

    public void setSpent(BigDecimal spent) {
        this.spent = spent;
    }

    public double getPercentage() {
        return percentage;
    }

    public void setPercentage(double percentage) {
        this.percentage = percentage;
    }
}
