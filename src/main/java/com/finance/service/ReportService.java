package com.finance.service;

import com.finance.dao.BudgetDAO;
import com.finance.dao.ExpenseDAO;
import com.finance.exception.DatabaseException;
import com.finance.model.Budget;
import com.finance.model.BudgetComparison;
import com.finance.model.CategorySummary;
import com.finance.model.Expense;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Business service handling financial reporting calculations.
 * Demonstrates meaningful Java Concurrency using ExecutorService and Callable tasks
 * to process spending metrics, category distributions, and budget comparisons in parallel.
 */
public class ReportService {

    private final ExpenseDAO expenseDAO;
    private final BudgetDAO budgetDAO;

    public ReportService() {
        this.expenseDAO = new ExpenseDAO();
        this.budgetDAO = new BudgetDAO();
    }

    public ReportService(ExpenseDAO expenseDAO, BudgetDAO budgetDAO) {
        this.expenseDAO = expenseDAO;
        this.budgetDAO = budgetDAO;
    }

    /**
     * Container object holding aggregated report results calculated asynchronously.
     */
    public static class ReportData {
        private final BigDecimal totalSpending;
        private final BigDecimal avgExpense;
        private final List<CategorySummary> categoryReportList;
        private final List<BudgetComparison> budgetComparisonList;
        private final List<String> categoriesList;

        public ReportData(BigDecimal totalSpending, BigDecimal avgExpense, 
                          List<CategorySummary> categoryReportList, 
                          List<BudgetComparison> budgetComparisonList, 
                          List<String> categoriesList) {
            this.totalSpending = totalSpending;
            this.avgExpense = avgExpense;
            this.categoryReportList = categoryReportList;
            this.budgetComparisonList = budgetComparisonList;
            this.categoriesList = categoriesList;
        }

        public BigDecimal getTotalSpending() { return totalSpending; }
        public BigDecimal getAvgExpense() { return avgExpense; }
        public List<CategorySummary> getCategoryReportList() { return categoryReportList; }
        public List<BudgetComparison> getBudgetComparisonList() { return budgetComparisonList; }
        public List<String> getCategoriesList() { return categoriesList; }
    }

    /**
     * Synthesizes financial report metrics asynchronously using Java Concurrency (ExecutorService).
     */
    public ReportData generateReport(String userId, String startDateStr, String endDateStr, String category) 
            throws DatabaseException {
        
        Date startDate = parseSqlDate(startDateStr);
        Date endDate = parseSqlDate(endDateStr);
        String catFilter = (category != null && !category.trim().isEmpty()) ? category.trim() : null;

        List<Expense> filteredExpenses = expenseDAO.findByFilter(userId, startDate, endDate, catFilter);
        List<Budget> userBudgets = budgetDAO.findByUserId(userId);

        // Standard thread pool size for report calculation tasks
        ExecutorService executor = Executors.newFixedThreadPool(3);

        try {
            // Task 1: Compute Total Spending & Average Expense Size
            Callable<BigDecimal[]> summaryTask = () -> {
                BigDecimal total = BigDecimal.ZERO;
                for (Expense e : filteredExpenses) {
                    total = total.add(e.getAmount());
                }
                BigDecimal avg = BigDecimal.ZERO;
                if (!filteredExpenses.isEmpty()) {
                    avg = total.divide(new BigDecimal(filteredExpenses.size()), 2, RoundingMode.HALF_UP);
                }
                return new BigDecimal[]{total, avg};
            };

            // Task 2: Compute Category Spending Breakdown List & Percentages
            Callable<List<CategorySummary>> categoryBreakdownTask = () -> {
                Map<String, BigDecimal> catTotals = new HashMap<>();
                BigDecimal total = BigDecimal.ZERO;
                for (Expense e : filteredExpenses) {
                    catTotals.put(e.getCategory(), catTotals.getOrDefault(e.getCategory(), BigDecimal.ZERO).add(e.getAmount()));
                    total = total.add(e.getAmount());
                }

                List<CategorySummary> list = new ArrayList<>();
                if (total.compareTo(BigDecimal.ZERO) > 0) {
                    for (Map.Entry<String, BigDecimal> entry : catTotals.entrySet()) {
                        double pct = entry.getValue().multiply(new BigDecimal("100"))
                                .divide(total, 2, RoundingMode.HALF_UP).doubleValue();
                        list.add(new CategorySummary(entry.getKey(), entry.getValue(), pct));
                    }
                }
                return list;
            };

            // Task 3: Compute Budget-vs-Actual Comparison Table
            Callable<List<BudgetComparison>> budgetComparisonTask = () -> {
                Map<String, BigDecimal> catTotals = new HashMap<>();
                for (Expense e : filteredExpenses) {
                    catTotals.put(e.getCategory(), catTotals.getOrDefault(e.getCategory(), BigDecimal.ZERO).add(e.getAmount()));
                }

                List<BudgetComparison> list = new ArrayList<>();
                for (Budget b : userBudgets) {
                    BigDecimal spent = catTotals.getOrDefault(b.getCategory(), BigDecimal.ZERO);
                    list.add(new BudgetComparison(b.getCategory(), b.getAmount(), spent));
                }
                return list;
            };

            // Submit tasks for concurrent execution
            Future<BigDecimal[]> summaryFuture = executor.submit(summaryTask);
            Future<List<CategorySummary>> categoryFuture = executor.submit(categoryBreakdownTask);
            Future<List<BudgetComparison>> budgetFuture = executor.submit(budgetComparisonTask);

            // Collect results from Futures
            BigDecimal[] summary = summaryFuture.get();
            List<CategorySummary> categoryList = categoryFuture.get();
            List<BudgetComparison> budgetList = budgetFuture.get();

            // Extract distinct categories for filter dropdown
            List<String> categoriesList = new ArrayList<>();
            Map<String, BigDecimal> allCategoryTotals = expenseDAO.getCategoryTotalsByUserId(userId);
            categoriesList.addAll(allCategoryTotals.keySet());

            return new ReportData(summary[0], summary[1], categoryList, budgetList, categoriesList);

        } catch (Exception e) {
            throw new DatabaseException("Error computing financial report metrics: " + e.getMessage(), e);
        } finally {
            executor.shutdown();
        }
    }

    private Date parseSqlDate(String dateStr) {
        if (dateStr == null || dateStr.trim().isEmpty()) {
            return null;
        }
        try {
            return Date.valueOf(dateStr.trim());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
