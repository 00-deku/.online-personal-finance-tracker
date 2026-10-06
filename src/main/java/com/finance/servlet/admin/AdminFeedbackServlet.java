package com.finance.servlet.admin;

import com.finance.exception.DatabaseException;
import com.finance.exception.ValidationException;
import com.finance.model.Feedback;
import com.finance.model.User;
import com.finance.service.FeedbackService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.List;

/**
 * Controller servlet managing feedback ticket queues and status transitions for Admin view.
 */
@WebServlet(urlPatterns = {"/admin/feedback", "/admin/update-feedback-status"})
public class AdminFeedbackServlet extends HttpServlet {

    private FeedbackService feedbackService;

    @Override
    public void init() throws ServletException {
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
            List<Feedback> feedbackList = feedbackService.getAllFeedback();
            request.setAttribute("feedbackList", feedbackList.isEmpty() ? null : feedbackList);
            request.getRequestDispatcher("/WEB-INF/views/admin/feedback.jsp").forward(request, response);

        } catch (DatabaseException e) {
            request.setAttribute("errorMessage", "Error loading feedback tickets: " + e.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/admin/feedback.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);
        User admin = (session != null) ? (User) session.getAttribute("user") : null;

        if (admin == null || !"ADMIN".equalsIgnoreCase(admin.getRole())) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        String path = request.getServletPath();

        if ("/admin/update-feedback-status".equals(path)) {
            String feedbackId = request.getParameter("id");
            String status = request.getParameter("status");

            try {
                feedbackService.updateStatus(feedbackId, status);
            } catch (ValidationException | DatabaseException e) {
                request.setAttribute("errorMessage", "Failed to update ticket status: " + e.getMessage());
            }
        }

        response.sendRedirect(request.getContextPath() + "/admin/feedback");
    }
}
