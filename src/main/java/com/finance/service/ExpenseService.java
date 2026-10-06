package com.finance.service;

import com.finance.dao.ExpenseDAO;
import com.finance.exception.DatabaseException;
import com.finance.exception.ValidationException;
import com.finance.model.CategorySummary;
import com.finance.model.Expense;
import com.finance.util.IDGenerator;
import com.finance.util.ValidationUtil;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Business service managing expense recording, updates, deletions, and category breakdowns.
 */
public class ExpenseService {

    private final ExpenseDAO expenseDAO;

    public ExpenseService() {
        this.expenseDAO = new ExpenseDAO();
    }

    public ExpenseService(ExpenseDAO expenseDAO) {
        this.expenseDAO = expenseDAO;
    }

    /**
     * Creates a new expense or updates an existing one if ID is provided.
     */
    public void saveExpense(String id, String category, String amountStr, String dateStr, String userId) 
            throws ValidationException, DatabaseException {
        
        if (ValidationUtil.isEmpty(category) || category.trim().length() > 15) {
            throw new ValidationException("Category is required and must be 15 characters or less.");
        }
        if (!ValidationUtil.isPositiveAmount(amountStr)) {
            throw new ValidationException("Please enter a valid positive expense amount.");
        }
        if (ValidationUtil.isEmpty(dateStr)) {
            throw new ValidationException("Expense date is required.");
        }
        if (ValidationUtil.isEmpty(userId)) {
            throw new ValidationException("User session expired.");
        }

        BigDecimal amount = new BigDecimal(amountStr.trim());
        Date date;
        try {
            date = Date.valueOf(dateStr.trim());
        } catch (IllegalArgumentException e) {
            throw new ValidationException("Invalid date format. Expected YYYY-MM-DD.");
        }

        if (ValidationUtil.isEmpty(id)) {
            // New Expense creation
            String newId = IDGenerator.generateId("EXP");
            Expense expense = new Expense(newId, category.trim(), amount, date, userId);
            expenseDAO.createExpense(expense);
        } else {
            // Existing Expense update
            Expense expense = new Expense(id.trim(), category.trim(), amount, date, userId);
            expenseDAO.updateExpense(expense);
        }
    }

    public void deleteExpense(String expenseId, String userId) throws DatabaseException {
        expenseDAO.deleteExpense(expenseId, userId);
    }

    public Expense getExpenseById(String id) throws DatabaseException {
        return expenseDAO.findById(id);
    }

    public List<Expense> getExpensesByUserId(String userId) throws DatabaseException {
        return expenseDAO.findByUserId(userId);
    }

    public List<Expense> getRecentExpenses(String userId, int limit) throws DatabaseException {
        return expenseDAO.findRecentByUserId(userId, limit);
    }

    public BigDecimal getTotalExpenses(String userId) throws DatabaseException {
        return expenseDAO.getTotalExpensesByUserId(userId);
    }

    public int getTotalSystemExpensesCount() throws DatabaseException {
        return expenseDAO.countAllExpenses();
    }

    /**
     * Calculates category spending breakdown list with percentages for dashboard and charts.
     */
    public List<CategorySummary> getCategoryBreakdown(String userId) throws DatabaseException {
        Map<String, BigDecimal> catTotals = expenseDAO.getCategoryTotalsByUserId(userId);
        BigDecimal totalSpent = expenseDAO.getTotalExpensesByUserId(userId);

        List<CategorySummary> list = new ArrayList<>();
        if (totalSpent.compareTo(BigDecimal.ZERO) == 0 || catTotals.isEmpty()) {
            return list;
        }

        for (Map.Entry<String, BigDecimal> entry : catTotals.entrySet()) {
            BigDecimal catTotal = entry.getValue();
            double pct = catTotal.multiply(new BigDecimal("100"))
                    .divide(totalSpent, 2, RoundingMode.HALF_UP)
                    .doubleValue();

            list.add(new CategorySummary(entry.getKey(), catTotal, pct));
        }

        return list;
    }
}
