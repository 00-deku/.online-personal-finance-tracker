package com.finance.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Date;

/**
 * Represents an individual financial expense item matching the 'EXPENSES' table schema.
 */
public class Expense implements Serializable {

    private static final long serialVersionUID = 1L;

    private String id;
    private String category;
    private BigDecimal amount;
    private Date date;
    private String userId;

    public Expense() {
    }

    public Expense(String id, String category, BigDecimal amount, Date date, String userId) {
        this.id = id;
        this.category = category;
        this.amount = amount;
        this.date = date;
        this.userId = userId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getUserId() {
        return userId;
    }

    // EL alias getter for JSP user_id property
    public String getUser_id() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
