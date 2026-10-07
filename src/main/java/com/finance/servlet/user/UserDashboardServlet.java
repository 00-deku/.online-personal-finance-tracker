package com.finance.servlet.user;

import com.finance.concurrent.ParallelBatch;
import com.finance.exception.DatabaseException;
import com.finance.model.Advice;
import com.finance.model.Budget;
import com.finance.model.CategorySummary;
import com.finance.model.Expense;
import com.finance.model.User;
import com.finance.service.AdviceService;
import com.finance.service.BudgetService;
import com.finance.service.ExpenseService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Controller servlet synthesizing financial summary cards and dashboard feeds for User view.
 */
@WebServlet("/user/dashboard")
public class UserDashboardServlet extends HttpServlet {

    private ExpenseService expenseService;
    private BudgetService budgetService;
    private AdviceService adviceService;

    @Override
    public void init() throws ServletException {
        this.expenseService = new ExpenseService();
        this.budgetService = new BudgetService();
        this.adviceService = new AdviceService();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);
        User user = (session != null) ? (User) session.getAttribute("user") : null;

        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        try {
            String userId = user.getId();

            // Fork the seven independent dashboard queries onto the shared worker pool.
            // Each DAO call opens its own JDBC connection, so they can run side by side.
            ParallelBatch batch = new ParallelBatch("User dashboard");
            CompletableFuture<BigDecimal> totalExpensesF = batch.fork("Total expenses", () -> expenseService.getTotalExpenses(userId));
            CompletableFuture<BigDecimal> totalBudgetF = batch.fork("Total budget", () -> budgetService.getTotalBudget(userId));
            CompletableFuture<BigDecimal> remainingF = batch.fork("Remaining budget", () -> budgetService.getRemainingBudget(userId));
            CompletableFuture<List<Expense>> recentF = batch.fork("Recent expenses", () -> expenseService.getRecentExpenses(userId, 5));
            CompletableFuture<List<CategorySummary>> breakdownF = batch.fork("Category breakdown", () -> expenseService.getCategoryBreakdown(userId));
            CompletableFuture<Budget> activeBudgetF = batch.fork("Active budget", () -> budgetService.getPrimaryActiveBudget(userId));
            CompletableFuture<List<Advice>> adviceF = batch.fork("Advisor advice", () -> adviceService.getAdviceForUser(userId));

            // The request thread waits once for all of them instead of seven times in a row.
            batch.awaitAll();

            BigDecimal totalExpenses = ParallelBatch.result(totalExpensesF);
            BigDecimal totalBudget = ParallelBatch.result(totalBudgetF);
            BigDecimal remainingBudget = ParallelBatch.result(remainingF);
            List<Expense> recentExpenses = ParallelBatch.result(recentF);
            List<CategorySummary> categoryBreakdown = ParallelBatch.result(breakdownF);
            Budget activeBudget = ParallelBatch.result(activeBudgetF);
            List<Advice> adviceList = ParallelBatch.result(adviceF);
            request.setAttribute("parallelBatch", batch);

            // Pass attributes to JSP template engine
            request.setAttribute("totalExpenses", totalExpenses != null ? totalExpenses : BigDecimal.ZERO);
            request.setAttribute("totalBudget", totalBudget != null ? totalBudget : BigDecimal.ZERO);
            request.setAttribute("remainingBudget", remainingBudget != null ? remainingBudget : BigDecimal.ZERO);

            request.setAttribute("recentExpenses", (recentExpenses != null && !recentExpenses.isEmpty()) ? recentExpenses : null);
            request.setAttribute("categoryBreakdown", (categoryBreakdown != null && !categoryBreakdown.isEmpty()) ? categoryBreakdown : null);
            request.setAttribute("activeBudget", activeBudget);
            request.setAttribute("adviceList", (adviceList != null && !adviceList.isEmpty()) ? adviceList : null);

            request.getRequestDispatcher("/WEB-INF/views/user/dashboard.jsp").forward(request, response);

        } catch (DatabaseException e) {
            request.setAttribute("errorMessage", "Error loading dashboard metrics: " + e.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/user/dashboard.jsp").forward(request, response);
        }
    }
}
