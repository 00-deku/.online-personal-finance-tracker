package com.finance.servlet.user;

import com.finance.exception.DatabaseException;
import com.finance.model.User;
import com.finance.service.ReportService;
import com.finance.service.ReportService.ReportData;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.math.BigDecimal;

/**
 * Controller servlet generating multi-threaded financial reports and budget variance analysis.
 */
@WebServlet("/user/reports")
public class ReportServlet extends HttpServlet {

    private ReportService reportService;

    @Override
    public void init() throws ServletException {
        this.reportService = new ReportService();
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

        String startDate = request.getParameter("startDate");
        String endDate = request.getParameter("endDate");
        String category = request.getParameter("category");

        try {
            ReportData reportData = reportService.generateReport(user.getId(), startDate, endDate, category);

            if (reportData.getTotalSpending().compareTo(BigDecimal.ZERO) > 0) {
                request.setAttribute("reportTotalSpending", reportData.getTotalSpending());
                request.setAttribute("reportAvgExpense", reportData.getAvgExpense());
                request.setAttribute("categoryReportList", reportData.getCategoryReportList().isEmpty() ? null : reportData.getCategoryReportList());
                request.setAttribute("budgetComparisonList", reportData.getBudgetComparisonList().isEmpty() ? null : reportData.getBudgetComparisonList());
            }

            request.setAttribute("categoriesList", reportData.getCategoriesList());
            request.setAttribute("parallelBatch", reportData.getBatch());
            request.getRequestDispatcher("/WEB-INF/views/user/reports.jsp").forward(request, response);

        } catch (DatabaseException e) {
            request.setAttribute("errorMessage", "Error compiling financial reports: " + e.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/user/reports.jsp").forward(request, response);
        }
    }
}
