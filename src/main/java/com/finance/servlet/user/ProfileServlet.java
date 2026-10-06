package com.finance.servlet.user;

import com.finance.exception.AuthenticationException;
import com.finance.exception.DatabaseException;
import com.finance.exception.ValidationException;
import com.finance.model.User;
import com.finance.service.UserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

/**
 * Controller servlet managing user account profile presentation and password changes.
 */
@WebServlet(urlPatterns = {"/user/profile", "/user/update-password"})
public class ProfileServlet extends HttpServlet {

    private UserService userService;

    @Override
    public void init() throws ServletException {
        this.userService = new UserService();
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

        request.getRequestDispatcher("/WEB-INF/views/user/profile.jsp").forward(request, response);
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

        if ("/user/update-password".equals(path)) {
            String oldPassword = request.getParameter("oldPassword");
            String newPassword = request.getParameter("newPassword");

            try {
                userService.changePassword(user.getId(), oldPassword, newPassword);
                request.setAttribute("successMessage", "Password updated successfully.");
            } catch (ValidationException | AuthenticationException e) {
                request.setAttribute("errorMessage", e.getMessage());
            } catch (DatabaseException e) {
                request.setAttribute("errorMessage", "Database error updating password.");
            }
        }

        request.getRequestDispatcher("/WEB-INF/views/user/profile.jsp").forward(request, response);
    }
}
