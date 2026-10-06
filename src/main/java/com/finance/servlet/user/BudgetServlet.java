package com.finance.servlet.user;

import com.finance.exception.DatabaseException;
import com.finance.exception.ValidationException;
import com.finance.model.Budget;
import com.finance.model.User;
import com.finance.service.BudgetService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.List;

/**
 * Controller servlet handling category budget target definitions and deletion.
 */
@WebServlet(urlPatterns = {"/user/budgets", "/user/add-budget", "/user/delete-budget"})
public class BudgetServlet extends HttpServlet {

    private BudgetService budgetService;

    @Override
    public void init() throws ServletException {
        this.budgetService = new BudgetService();
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
            List<Budget> budgetsList = budgetService.getUserBudgetsWithSpent(user.getId());
            request.setAttribute("budgetsList", budgetsList.isEmpty() ? null : budgetsList);
            request.getRequestDispatcher("/WEB-INF/views/user/budgets.jsp").forward(request, response);

        } catch (DatabaseException e) {
            request.setAttribute("errorMessage", "Error loading user budgets: " + e.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/user/budgets.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);
        User user = (session != null) ? (User) session.getAttribute("user") : null;

        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        String path = request.getServletPath();

        try {
            if ("/user/delete-budget".equals(path)) {
                String budgetId = request.getParameter("id");
                if (budgetId != null && !budgetId.trim().isEmpty()) {
                    budgetService.deleteBudget(budgetId.trim(), user.getId());
                }
                response.sendRedirect(request.getContextPath() + "/user/budgets");

            } else if ("/user/add-budget".equals(path)) {
                String category = request.getParameter("category");
                String amount = request.getParameter("amount");
                String period = request.getParameter("period");

                budgetService.saveBudget(category, amount, period, user.getId());
                response.sendRedirect(request.getContextPath() + "/user/budgets");
            }

        } catch (ValidationException e) {
            request.setAttribute("errorMessage", e.getMessage());
            doGet(request, response);
        } catch (DatabaseException e) {
            request.setAttribute("errorMessage", "Database error saving budget target.");
            doGet(request, response);
        }
    }
}
