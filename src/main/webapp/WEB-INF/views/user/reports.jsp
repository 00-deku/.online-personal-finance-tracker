<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<jsp:include page="../common/header.jsp">
    <jsp:param name="pageTitle" value="Reports - Finance."/>
</jsp:include>

<jsp:include page="../common/navbar.jsp">
    <jsp:param name="role" value="USER"/>
</jsp:include>

<div class="app-container">
    <jsp:include page="../common/sidebar.jsp">
        <jsp:param name="activePage" value="reports"/>
    </jsp:include>

    <main class="main-content">
        <div class="content-wrapper">

            <c:if test="${not empty errorMessage}">
                <div class="alert alert-error mb-4">
                    ${errorMessage}
                </div>
            </c:if>

            <div class="page-header">
                <div>
                    <h1 class="page-title">Reports</h1>
                    <p class="page-subtitle">Period spending summaries and budget variance analysis.</p>
                </div>
            </div>

            <!-- Date Range Filter Form -->
            <div class="card mb-4">
                <form method="get" action="${pageContext.request.contextPath}/user/reports" class="flex gap-3" style="align-items: flex-end; flex-wrap: wrap;">
                    <div class="form-group mb-0" style="flex: 1; min-width: 160px;">
                        <label for="startDate" class="form-label">From Date</label>
                        <input type="date" id="startDate" name="startDate" class="form-control" value="${param.startDate}">
                    </div>

                    <div class="form-group mb-0" style="flex: 1; min-width: 160px;">
                        <label for="endDate" class="form-label">To Date</label>
                        <input type="date" id="endDate" name="endDate" class="form-control" value="${param.endDate}">
                    </div>

                    <div class="form-group mb-0" style="flex: 1; min-width: 160px;">
                        <label for="categoryFilter" class="form-label">Category</label>
                        <select id="categoryFilter" name="category" class="form-control">
                            <option value="">All Categories</option>
                            <c:forEach items="${categoriesList}" var="cat">
                                <option value="${cat}" ${param.category == cat ? 'selected' : ''}>${cat}</option>
                            </c:forEach>
                        </select>
                    </div>

                    <button type="submit" class="btn btn-secondary">Filter</button>
                </form>
            </div>

            <!-- Data Driven Report Displays -->
            <c:choose>
                <c:when test="${not empty reportTotalSpending}">
                    
                    <div class="stats-grid mb-4">
                        <div class="stat-card">
                            <div class="stat-label">Period Spending</div>
                            <div class="stat-value">₹${reportTotalSpending}</div>
                            <div class="stat-subtext">Total for selected range</div>
                        </div>

                        <div class="stat-card">
                            <div class="stat-label">Average Expense</div>
                            <div class="stat-value">₹${reportAvgExpense}</div>
                            <div class="stat-subtext">Mean transaction size</div>
                        </div>
                    </div>

                    <div class="dashboard-grid">
                        <div class="card">
                            <div class="card-header">
                                <div class="card-title">Category Breakdown</div>
                            </div>
                            <div class="chart-bar-group">
                                <c:forEach items="${categoryReportList}" var="row">
                                    <div class="chart-bar-row">
                                        <div class="chart-bar-label">
                                            <span>${row.category}</span>
                                            <span class="font-mono">₹${row.amount} (${row.percentage}%)</span>
                                        </div>
                                        <div class="chart-bar-bg">
                                            <div class="chart-bar-fill" style="width: ${row.percentage}%;"></div>
                                        </div>
                                    </div>
                                </c:forEach>
                            </div>
                        </div>

                        <div class="card">
                            <div class="card-header">
                                <div class="card-title">Budget Comparison</div>
                            </div>
                            <div class="table-container">
                                <table class="table">
                                    <thead>
                                        <tr>
                                            <th>Category</th>
                                            <th>Budget</th>
                                            <th>Spent</th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        <c:forEach items="${budgetComparisonList}" var="comp">
                                            <tr>
                                                <td>${comp.category}</td>
                                                <td class="font-mono">₹${comp.budget}</td>
                                                <td class="font-mono">₹${comp.spent}</td>
                                            </tr>
                                        </c:forEach>
                                    </tbody>
                                </table>
                            </div>
                        </div>
                    </div>

                </c:when>
                <c:otherwise>
                    <div class="empty-state">
                        <div class="empty-state-title">No report data available.</div>
                        <div class="empty-state-text">Reports will appear once sufficient expense data is available.</div>
                    </div>
                </c:otherwise>
            </c:choose>

            <jsp:include page="../common/parallel-timeline.jsp"/>

        </div>
<jsp:include page="../common/footer.jsp"/>

