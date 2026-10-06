package com.finance.service;

import com.finance.dao.BudgetDAO;
import com.finance.dao.ExpenseDAO;
import com.finance.exception.DatabaseException;
import com.finance.exception.ValidationException;
import com.finance.model.Budget;
import com.finance.util.IDGenerator;
import com.finance.util.ValidationUtil;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;

/**
 * Service managing user budget targets, spending limit allocations, and budget variance metrics.
 */
public class BudgetService {

    private final BudgetDAO budgetDAO;
    private final ExpenseDAO expenseDAO;

    public BudgetService() {
        this.budgetDAO = new BudgetDAO();
        this.expenseDAO = new ExpenseDAO();
    }

    public BudgetService(BudgetDAO budgetDAO, ExpenseDAO expenseDAO) {
        this.budgetDAO = budgetDAO;
        this.expenseDAO = expenseDAO;
    }

    /**
     * Creates or updates a target budget limit for a category and period.
     */
    public void saveBudget(String category, String amountStr, String period, String userId) 
            throws ValidationException, DatabaseException {
        
        if (ValidationUtil.isEmpty(category) || category.trim().length() > 20) {
            throw new ValidationException("Category is required and must be 20 characters or less.");
        }
        if (!ValidationUtil.isPositiveAmount(amountStr)) {
            throw new ValidationException("Please enter a valid positive target budget amount.");
        }
        if (ValidationUtil.isEmpty(period)) {
            throw new ValidationException("Period is required.");
        }
        if (ValidationUtil.isEmpty(userId)) {
            throw new ValidationException("User session expired.");
        }

        BigDecimal amount = new BigDecimal(amountStr.trim());
        String budgetId = IDGenerator.generateId("BGT");

        Budget budget = new Budget(budgetId, userId, category.trim(), amount, period.trim());
        budgetDAO.saveOrUpdateBudget(budget);
    }

    public void deleteBudget(String budgetId, String userId) throws DatabaseException {
        budgetDAO.deleteBudget(budgetId, userId);
    }

    /**
     * Retrieves all user budgets populated with dynamic 'spent' and 'percentage' calculation.
     */
    public List<Budget> getUserBudgetsWithSpent(String userId) throws DatabaseException {
        List<Budget> budgets = budgetDAO.findByUserId(userId);
        Map<String, BigDecimal> categoryTotals = expenseDAO.getCategoryTotalsByUserId(userId);

        for (Budget b : budgets) {
            BigDecimal spent = categoryTotals.getOrDefault(b.getCategory(), BigDecimal.ZERO);
            b.setSpent(spent);

            if (b.getAmount().compareTo(BigDecimal.ZERO) > 0) {
                double pct = spent.multiply(new BigDecimal("100"))
                        .divide(b.getAmount(), 2, RoundingMode.HALF_UP)
                        .doubleValue();
                b.setPercentage(Math.min(pct, 100.0));
            } else {
                b.setPercentage(0.0);
            }
        }

        return budgets;
    }

    /**
     * Retrieves overall total active budget sum across categories.
     */
    public BigDecimal getTotalBudget(String userId) throws DatabaseException {
        return budgetDAO.getTotalBudgetByUserId(userId);
    }

    /**
     * Calculates remaining available buffer (Total Budget - Total Expenses).
     */
    public BigDecimal getRemainingBudget(String userId) throws DatabaseException {
        BigDecimal totalBudget = budgetDAO.getTotalBudgetByUserId(userId);
        BigDecimal totalExpenses = expenseDAO.getTotalExpensesByUserId(userId);
        BigDecimal remaining = totalBudget.subtract(totalExpenses);
        
        return remaining.compareTo(BigDecimal.ZERO) < 0 ? BigDecimal.ZERO : remaining;
    }

    /**
     * Gets primary active budget for display on the dashboard card.
     */
    public Budget getPrimaryActiveBudget(String userId) throws DatabaseException {
        List<Budget> list = getUserBudgetsWithSpent(userId);
        return list.isEmpty() ? null : list.get(0);
    }
}
