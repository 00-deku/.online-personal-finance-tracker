package com.finance.model;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Data Transfer Object (DTO) comparing budget allocations versus actual category spending for reports.
 */
public class BudgetComparison implements Serializable {

    private static final long serialVersionUID = 1L;

    private String category;
    private BigDecimal budget;
    private BigDecimal spent;

    public BudgetComparison() {
    }

    public BudgetComparison(String category, BigDecimal budget, BigDecimal spent) {
        this.category = category;
        this.budget = budget;
        this.spent = spent;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public BigDecimal getBudget() {
        return budget;
    }

    public void setBudget(BigDecimal budget) {
        this.budget = budget;
    }

    public BigDecimal getSpent() {
        return spent;
    }

    public void setSpent(BigDecimal spent) {
        this.spent = spent;
    }
}
