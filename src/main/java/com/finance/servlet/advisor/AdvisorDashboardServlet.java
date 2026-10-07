package com.finance.servlet.advisor;

import com.finance.concurrent.ParallelBatch;
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
import java.util.concurrent.CompletableFuture;

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

            ParallelBatch batch = new ParallelBatch("Advisor dashboard");
            CompletableFuture<Integer> adviceCountF = batch.fork("Issued advice count", () -> adviceService.getIssuedAdviceCount(advisorId));
            CompletableFuture<Integer> uniqueF = batch.fork("Unique users advised", () -> adviceService.getUniqueUsersAdvisedCount(advisorId));
            CompletableFuture<List<Advice>> adviceF = batch.fork("Advice history", () -> adviceService.getAdviceByAdvisor(advisorId));
            CompletableFuture<List<User>> clientsF = batch.fork("Client list", userService::getAllUsers);
            batch.awaitAll();

            int adviceCount = ParallelBatch.result(adviceCountF);
            int uniqueUsersAdvised = ParallelBatch.result(uniqueF);
            List<Advice> adviceList = ParallelBatch.result(adviceF);
            List<User> clientUserList = ParallelBatch.result(clientsF);
            request.setAttribute("parallelBatch", batch);

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
