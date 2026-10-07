package com.finance.servlet.admin;

import com.finance.concurrent.ParallelBatch;
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
import java.util.concurrent.CompletableFuture;

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
            // Counts and lists are independent queries, so load them in parallel.
            ParallelBatch batch = new ParallelBatch("Admin dashboard");
            CompletableFuture<Integer> userCountF = batch.fork("User count", userService::getTotalUserCount);
            CompletableFuture<Integer> expenseCountF = batch.fork("Expense count", expenseService::getTotalSystemExpensesCount);
            CompletableFuture<Integer> pendingF = batch.fork("Pending feedback count", feedbackService::getPendingFeedbackCount);
            CompletableFuture<List<User>> usersF = batch.fork("All users", userService::getAllUsers);
            CompletableFuture<List<Feedback>> feedbackF = batch.fork("All feedback", feedbackService::getAllFeedback);
            batch.awaitAll();

            int totalUserCount = ParallelBatch.result(userCountF);
            int totalExpensesCount = ParallelBatch.result(expenseCountF);
            int pendingFeedbackCount = ParallelBatch.result(pendingF);
            List<User> recentUserList = ParallelBatch.result(usersF);
            List<Feedback> feedbackList = ParallelBatch.result(feedbackF);
            request.setAttribute("parallelBatch", batch);

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
