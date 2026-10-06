package com.finance.servlet.admin;

import com.finance.exception.DatabaseException;
import com.finance.model.Feedback;
import com.finance.model.User;
import com.finance.service.ExpenseService;
import com.finance.service.FeedbackService;
import com.finance.service.UserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.List;

/**
 * Controller servlet presenting system overview statistics, user queues, and pending feedback for Admin view.
 */
@WebServlet("/admin/dashboard")
public class AdminDashboardServlet extends HttpServlet {

    private UserService userService;
    private ExpenseService expenseService;
    private FeedbackService feedbackService;

    @Override
    public void init() throws ServletException {
        this.userService = new UserService();
        this.expenseService = new ExpenseService();
        this.feedbackService = new FeedbackService();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);
        User admin = (session != null) ? (User) session.getAttribute("user") : null;

        if (admin == null || !"ADMIN".equalsIgnoreCase(admin.getRole())) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        try {
            int totalUserCount = userService.getTotalUserCount();
            int totalExpensesCount = expenseService.getTotalSystemExpensesCount();
            int pendingFeedbackCount = feedbackService.getPendingFeedbackCount();

            List<User> recentUserList = userService.getAllUsers();
            List<Feedback> feedbackList = feedbackService.getAllFeedback();

            request.setAttribute("totalUserCount", totalUserCount);
            request.setAttribute("totalExpensesCount", totalExpensesCount);
            request.setAttribute("pendingFeedbackCount", pendingFeedbackCount);

            request.setAttribute("recentUserList", recentUserList.isEmpty() ? null : recentUserList);
            request.setAttribute("feedbackList", feedbackList.isEmpty() ? null : feedbackList);


            request.getRequestDispatcher("/WEB-INF/views/admin/dashboard.jsp").forward(request, response);

        } catch (DatabaseException e) {
            request.setAttribute("errorMessage", "Error loading admin dashboard metrics: " + e.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/admin/dashboard.jsp").forward(request, response);
        }
    }
}
