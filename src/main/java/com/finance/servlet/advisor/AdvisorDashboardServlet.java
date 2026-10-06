package com.finance.servlet.advisor;

import com.finance.exception.DatabaseException;
import com.finance.model.Advice;
import com.finance.model.User;
import com.finance.service.AdviceService;
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
 * Controller servlet synthesizing Advisor dashboard statistics and client recommendation logs.
 */
@WebServlet("/advisor/dashboard")
public class AdvisorDashboardServlet extends HttpServlet {

    private AdviceService adviceService;
    private UserService userService;

    @Override
    public void init() throws ServletException {
        this.adviceService = new AdviceService();
        this.userService = new UserService();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);
        User advisor = (session != null) ? (User) session.getAttribute("user") : null;

        if (advisor == null || !"ADVISOR".equalsIgnoreCase(advisor.getRole()) && !"ADMIN".equalsIgnoreCase(advisor.getRole())) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        try {
            String advisorId = advisor.getId();

            int adviceCount = adviceService.getIssuedAdviceCount(advisorId);
            int uniqueUsersAdvised = adviceService.getUniqueUsersAdvisedCount(advisorId);
            List<Advice> adviceList = adviceService.getAdviceByAdvisor(advisorId);
            List<User> clientUserList = userService.getAllUsers();

            // Pass numeric metrics directly (0 is a valid value)
            request.setAttribute("adviceCount", adviceCount);
            request.setAttribute("uniqueUsersAdvised", uniqueUsersAdvised);
            request.setAttribute("adviceList", adviceList.isEmpty() ? null : adviceList);
            request.setAttribute("clientUserList", clientUserList.isEmpty() ? null : clientUserList);

            request.getRequestDispatcher("/WEB-INF/views/advisor/dashboard.jsp").forward(request, response);

        } catch (DatabaseException e) {
            request.setAttribute("errorMessage", "Error loading advisor overview: " + e.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/advisor/dashboard.jsp").forward(request, response);
        }
    }
}
