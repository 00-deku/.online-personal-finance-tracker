package com.finance.servlet.advisor;

import com.finance.exception.DatabaseException;
import com.finance.exception.ValidationException;
import com.finance.model.Advice;
import com.finance.model.Expense;
import com.finance.model.User;
import com.finance.service.AdviceService;
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
 * Controller servlet handling creation and management of financial advice issued to client users.
 */
@WebServlet(urlPatterns = {"/advisor/advice", "/advisor/send-advice", "/advisor/delete-advice"})
public class AdviceServlet extends HttpServlet {

    private AdviceService adviceService;
    private UserService userService;
    private ExpenseService expenseService;
    private FeedbackService feedbackService;

    @Override
    public void init() throws ServletException {
        this.adviceService = new AdviceService();
        this.userService = new UserService();
        this.expenseService = new ExpenseService();
        this.feedbackService = new FeedbackService();
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
            List<Advice> adviceList = adviceService.getAdviceByAdvisor(advisor.getId());
            List<User> userList = userService.getAllUsers();

            String selectedUserId = request.getParameter("selectedUserId");
            if (selectedUserId != null && !selectedUserId.trim().isEmpty()) {
                selectedUserId = selectedUserId.trim();
                List<Expense> selectedUserExpenses = expenseService.getExpensesByUserId(selectedUserId);
                request.setAttribute("selectedUserId", selectedUserId);
                request.setAttribute("selectedUserExpenses", selectedUserExpenses.isEmpty() ? null : selectedUserExpenses);
            }

            request.setAttribute("userList", userList.isEmpty() ? null : userList);
            request.setAttribute("adviceList", adviceList.isEmpty() ? null : adviceList);
            request.getRequestDispatcher("/WEB-INF/views/advisor/advice.jsp").forward(request, response);

        } catch (DatabaseException e) {
            request.setAttribute("errorMessage", "Error retrieving advice data: " + e.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/advisor/advice.jsp").forward(request, response);
        }
    }


    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);
        User advisor = (session != null) ? (User) session.getAttribute("user") : null;

        if (advisor == null || !"ADVISOR".equalsIgnoreCase(advisor.getRole()) && !"ADMIN".equalsIgnoreCase(advisor.getRole())) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        String path = request.getServletPath();

        try {
            if ("/advisor/delete-advice".equals(path)) {
                String adviceId = request.getParameter("id");
                if (adviceId != null && !adviceId.trim().isEmpty()) {
                    adviceService.deleteAdvice(adviceId.trim(), advisor.getId());
                }
                response.sendRedirect(request.getContextPath() + "/advisor/advice");

            } else if ("/advisor/send-advice".equals(path)) {
                String targetUserId = request.getParameter("user_id");
                String date = request.getParameter("date");
                String message = request.getParameter("message");

                adviceService.sendAdvice(advisor.getId(), targetUserId, date, message);
                response.sendRedirect(request.getContextPath() + "/advisor/advice");
            }

        } catch (ValidationException e) {
            request.setAttribute("errorMessage", e.getMessage());
            doGet(request, response);
        } catch (DatabaseException e) {
            request.setAttribute("errorMessage", "Database error sending advice recommendation.");
            doGet(request, response);
        }
    }
}
