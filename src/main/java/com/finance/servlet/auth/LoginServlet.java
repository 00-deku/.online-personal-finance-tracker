package com.finance.servlet.auth;

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
 * Controller servlet managing user authentication (login).
 */
@WebServlet("/login")
public class LoginServlet extends HttpServlet {

    private UserService userService;

    @Override
    public void init() throws ServletException {
        this.userService = new UserService();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        // Forward request to login JSP view
        request.getRequestDispatcher("/WEB-INF/views/auth/login.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String email = request.getParameter("email");
        String password = request.getParameter("password");

        try {
            User user = userService.login(email, password);

            // Establish authenticated session
            HttpSession session = request.getSession(true);
            session.setAttribute("user", user);

            // Role-based redirection upon successful login
            String role = user.getRole();
            if ("ADMIN".equalsIgnoreCase(role)) {
                response.sendRedirect(request.getContextPath() + "/admin/dashboard");
            } else if ("ADVISOR".equalsIgnoreCase(role)) {
                response.sendRedirect(request.getContextPath() + "/advisor/dashboard");
            } else {
                response.sendRedirect(request.getContextPath() + "/user/dashboard");
            }

        } catch (ValidationException | AuthenticationException e) {
            request.setAttribute("errorMessage", e.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/auth/login.jsp").forward(request, response);
        } catch (DatabaseException e) {
            request.setAttribute("errorMessage", "Database error encountered during authentication.");
            request.getRequestDispatcher("/WEB-INF/views/auth/login.jsp").forward(request, response);
        }
    }
}
