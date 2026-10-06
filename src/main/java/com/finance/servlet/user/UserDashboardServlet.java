package com.finance.servlet.user;

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

            // Load financial metrics
            BigDecimal totalExpenses = expenseService.getTotalExpenses(userId);
            BigDecimal totalBudget = budgetService.getTotalBudget(userId);
            BigDecimal remainingBudget = budgetService.getRemainingBudget(userId);

            List<Expense> recentExpenses = expenseService.getRecentExpenses(userId, 5);
            List<CategorySummary> categoryBreakdown = expenseService.getCategoryBreakdown(userId);
            Budget activeBudget = budgetService.getPrimaryActiveBudget(userId);
            List<Advice> adviceList = adviceService.getAdviceForUser(userId);

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
