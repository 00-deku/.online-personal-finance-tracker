package com.finance.servlet.admin;

import com.finance.exception.DatabaseException;
import com.finance.model.User;
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
 * Controller servlet handling user account listing and transactional account deletion for Admin users.
 */
@WebServlet(urlPatterns = {"/admin/users", "/admin/delete-user"})
public class AdminUserServlet extends HttpServlet {

    private UserService userService;

    @Override
    public void init() throws ServletException {
        this.userService = new UserService();
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
            List<User> userList = userService.getAllUsers();
            request.setAttribute("userList", userList.isEmpty() ? null : userList);
            request.getRequestDispatcher("/WEB-INF/views/admin/users.jsp").forward(request, response);

        } catch (DatabaseException e) {
            request.setAttribute("errorMessage", "Error retrieving registered user list: " + e.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/admin/users.jsp").forward(request, response);
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

        if ("/admin/delete-user".equals(path)) {
            String userId = request.getParameter("id");
            if (userId != null && !userId.trim().isEmpty()) {
                try {
                    // Executes cascade user deletion wrapped in a single JDBC Transaction
                    userService.deleteUser(userId.trim());
                } catch (DatabaseException e) {
                    request.setAttribute("errorMessage", "Failed to delete user account: " + e.getMessage());
                }
            }
        }

        response.sendRedirect(request.getContextPath() + "/admin/users");
    }
}
