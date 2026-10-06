package com.finance.servlet.user;

import com.finance.exception.DatabaseException;
import com.finance.exception.ValidationException;
import com.finance.model.User;
import com.finance.service.FeedbackService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

/**
 * Controller servlet handling user support feedback ticket submissions.
 */
@WebServlet("/feedback/submit")
public class FeedbackServlet extends HttpServlet {

    private FeedbackService feedbackService;

    @Override
    public void init() throws ServletException {
        this.feedbackService = new FeedbackService();
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

        String message = request.getParameter("message");

        try {
            feedbackService.submitFeedback(user.getId(), message);
            request.setAttribute("successMessage", "Feedback ticket submitted successfully.");
        } catch (ValidationException e) {
            request.setAttribute("errorMessage", e.getMessage());
        } catch (DatabaseException e) {
            request.setAttribute("errorMessage", "Database error submitting feedback.");
        }

        request.getRequestDispatcher("/WEB-INF/views/user/profile.jsp").forward(request, response);
    }
}
