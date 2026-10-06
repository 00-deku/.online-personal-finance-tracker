package com.finance.servlet.user;

import com.finance.exception.DatabaseException;
import com.finance.exception.ValidationException;
import com.finance.model.Expense;
import com.finance.model.User;
import com.finance.service.ExpenseService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.List;

/**
 * Controller servlet handling expense listing, creation, modification, and deletion.
 */
@WebServlet(urlPatterns = {"/user/expenses", "/user/add-expense", "/user/delete-expense"})
public class ExpenseServlet extends HttpServlet {

    private ExpenseService expenseService;

    @Override
    public void init() throws ServletException {
        this.expenseService = new ExpenseService();
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

        String path = request.getServletPath();

        try {
            if ("/user/add-expense".equals(path)) {
                // If edit mode (id parameter present), populate expense details
                String expenseId = request.getParameter("id");
                if (expenseId != null && !expenseId.trim().isEmpty()) {
                    Expense expense = expenseService.getExpenseById(expenseId.trim());
                    if (expense != null && user.getId().equals(expense.getUserId())) {
                        request.setAttribute("expense", expense);
                    }
                }
                request.getRequestDispatcher("/WEB-INF/views/user/add-expense.jsp").forward(request, response);

            } else {
                // Default: /user/expenses list view
                List<Expense> expensesList = expenseService.getExpensesByUserId(user.getId());
                request.setAttribute("expensesList", expensesList.isEmpty() ? null : expensesList);
                request.getRequestDispatcher("/WEB-INF/views/user/expenses.jsp").forward(request, response);
            }

        } catch (DatabaseException e) {
            request.setAttribute("errorMessage", "Database error handling expenses: " + e.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/user/expenses.jsp").forward(request, response);
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
            if ("/user/delete-expense".equals(path)) {
                String expenseId = request.getParameter("id");
                if (expenseId != null && !expenseId.trim().isEmpty()) {
                    expenseService.deleteExpense(expenseId.trim(), user.getId());
                }
                response.sendRedirect(request.getContextPath() + "/user/expenses");

            } else if ("/user/add-expense".equals(path)) {
                String id = request.getParameter("id");
                String category = request.getParameter("category");
                String amount = request.getParameter("amount");
                String date = request.getParameter("date");

                expenseService.saveExpense(id, category, amount, date, user.getId());
                response.sendRedirect(request.getContextPath() + "/user/expenses");
            }

        } catch (ValidationException e) {
            request.setAttribute("errorMessage", e.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/user/add-expense.jsp").forward(request, response);
        } catch (DatabaseException e) {
            request.setAttribute("errorMessage", "Database error saving expense transaction.");
            request.getRequestDispatcher("/WEB-INF/views/user/add-expense.jsp").forward(request, response);
        }
    }
}
