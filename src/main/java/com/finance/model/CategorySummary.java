package com.finance.model;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Data Transfer Object (DTO) used for reporting category spending breakdowns and progress charts.
 */
public class CategorySummary implements Serializable {

    private static final long serialVersionUID = 1L;

    private String category;
    private BigDecimal amount;
    private double percentage;

    public CategorySummary() {
    }

    public CategorySummary(String category, BigDecimal amount, double percentage) {
        this.category = category;
        this.amount = amount;
        this.percentage = percentage;
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

    public double getPercentage() {
        return percentage;
    }

    public void setPercentage(double percentage) {
        this.percentage = percentage;
    }
}
