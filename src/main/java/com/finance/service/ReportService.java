package com.finance.service;

import com.finance.concurrent.ParallelBatch;
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
import java.util.concurrent.CompletableFuture;

/**
 * Business service handling financial reporting calculations.
 * Uses the shared worker pool (see {@link com.finance.concurrent.TaskExecutor}) to load data
 * and compute spending metrics, category distributions, and budget comparisons in parallel.
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
        private final ParallelBatch batch;

        public ReportData(BigDecimal totalSpending, BigDecimal avgExpense, 
                          List<CategorySummary> categoryReportList, 
                          List<BudgetComparison> budgetComparisonList, 
                          List<String> categoriesList,
                          ParallelBatch batch) {
            this.totalSpending = totalSpending;
            this.avgExpense = avgExpense;
            this.categoryReportList = categoryReportList;
            this.budgetComparisonList = budgetComparisonList;
            this.categoriesList = categoriesList;
            this.batch = batch;
        }

        public BigDecimal getTotalSpending() { return totalSpending; }
        public BigDecimal getAvgExpense() { return avgExpense; }
        public List<CategorySummary> getCategoryReportList() { return categoryReportList; }
        public List<BudgetComparison> getBudgetComparisonList() { return budgetComparisonList; }
        public List<String> getCategoriesList() { return categoriesList; }
        public ParallelBatch getBatch() { return batch; }
    }

    /**
     * Builds the report in two parallel phases on the shared worker pool.
     *
     * Phase 1 (I/O-bound): the three database reads are independent, so they are forked together.
     * Phase 2 (CPU-bound): the three calculations only read the immutable results of phase 1,
     * so they can also run side by side without locking.
     */
    public ReportData generateReport(String userId, String startDateStr, String endDateStr, String category) 
            throws DatabaseException {
        
        Date startDate = parseSqlDate(startDateStr);
        Date endDate = parseSqlDate(endDateStr);
        String catFilter = (category != null && !category.trim().isEmpty()) ? category.trim() : null;

        ParallelBatch batch = new ParallelBatch("Financial report");

        // Phase 1: parallel database reads
        CompletableFuture<List<Expense>> expensesF = batch.fork("Load filtered expenses",
                () -> expenseDAO.findByFilter(userId, startDate, endDate, catFilter));
        CompletableFuture<List<Budget>> budgetsF = batch.fork("Load budgets",
                () -> budgetDAO.findByUserId(userId));
        CompletableFuture<Map<String, BigDecimal>> allCategoriesF = batch.fork("Load category list",
                () -> expenseDAO.getCategoryTotalsByUserId(userId));

        // Phase 2: each calculation starts as soon as the data it needs is ready
        CompletableFuture<BigDecimal[]> summaryF = expensesF.thenCompose(expenses ->
                batch.fork("Compute total & average", () -> computeSummary(expenses)));
        CompletableFuture<List<CategorySummary>> breakdownF = expensesF.thenCompose(expenses ->
                batch.fork("Compute category breakdown", () -> computeCategoryBreakdown(expenses)));
        CompletableFuture<List<BudgetComparison>> comparisonF = expensesF.thenCombine(budgetsF, Pair::new)
                .thenCompose(pair -> batch.fork("Compute budget vs actual",
                        () -> computeBudgetComparison(pair.expenses, pair.budgets)));

        CompletableFuture.allOf(summaryF, breakdownF, comparisonF, allCategoriesF).exceptionally(e -> null).join();
        batch.awaitAll();

        BigDecimal[] summary = ParallelBatch.result(summaryF);
        List<String> categoriesList = new ArrayList<>(ParallelBatch.result(allCategoriesF).keySet());

        return new ReportData(summary[0], summary[1], ParallelBatch.result(breakdownF),
                ParallelBatch.result(comparisonF), categoriesList, batch);
    }

    private record Pair(List<Expense> expenses, List<Budget> budgets) {
    }

    private BigDecimal[] computeSummary(List<Expense> expenses) {
        BigDecimal total = BigDecimal.ZERO;
        for (Expense e : expenses) {
            total = total.add(e.getAmount());
        }
        BigDecimal avg = BigDecimal.ZERO;
        if (!expenses.isEmpty()) {
            avg = total.divide(new BigDecimal(expenses.size()), 2, RoundingMode.HALF_UP);
        }
        return new BigDecimal[]{total, avg};
    }

    private List<CategorySummary> computeCategoryBreakdown(List<Expense> expenses) {
        Map<String, BigDecimal> catTotals = new HashMap<>();
        BigDecimal total = BigDecimal.ZERO;
        for (Expense e : expenses) {
            catTotals.merge(e.getCategory(), e.getAmount(), BigDecimal::add);
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
    }

    private List<BudgetComparison> computeBudgetComparison(List<Expense> expenses, List<Budget> budgets) {
        Map<String, BigDecimal> catTotals = new HashMap<>();
        for (Expense e : expenses) {
            catTotals.merge(e.getCategory(), e.getAmount(), BigDecimal::add);
        }

        List<BudgetComparison> list = new ArrayList<>();
        for (Budget b : budgets) {
            BigDecimal spent = catTotals.getOrDefault(b.getCategory(), BigDecimal.ZERO);
            list.add(new BudgetComparison(b.getCategory(), b.getAmount(), spent));
        }
        return list;
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
