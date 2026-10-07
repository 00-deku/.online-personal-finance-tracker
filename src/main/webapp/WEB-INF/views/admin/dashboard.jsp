<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<jsp:include page="../common/header.jsp">
    <jsp:param name="pageTitle" value="Admin Dashboard - Finance."/>
</jsp:include>

<jsp:include page="../common/navbar.jsp">
    <jsp:param name="role" value="ADMIN"/>
</jsp:include>

<div class="app-container">
    <jsp:include page="../common/sidebar.jsp">
        <jsp:param name="activePage" value="admin-dashboard"/>
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
                    <h1 class="page-title">Admin Dashboard</h1>
                    <p class="page-subtitle">System administration overview.</p>
                </div>
            </div>

            <!-- Stats Grid -->
            <div class="stats-grid mb-4">
                <div class="stat-card">
                    <div class="stat-label">Total Users</div>
                    <div class="stat-value"><c:choose><c:when test="${not empty totalUserCount}">${totalUserCount}</c:when><c:otherwise>0</c:otherwise></c:choose></div>
                    <div class="stat-subtext">Registered accounts</div>
                </div>

                <div class="stat-card">
                    <div class="stat-label">Total Expenses</div>
                    <div class="stat-value"><c:choose><c:when test="${not empty totalExpensesCount}">${totalExpensesCount}</c:when><c:otherwise>0</c:otherwise></c:choose></div>
                    <div class="stat-subtext">System transactions</div>
                </div>

                <div class="stat-card">
                    <div class="stat-label">Pending Feedback</div>
                    <div class="stat-value"><c:choose><c:when test="${not empty pendingFeedbackCount}">${pendingFeedbackCount}</c:when><c:otherwise>0</c:otherwise></c:choose></div>
                    <div class="stat-subtext">Tickets needing review</div>
                </div>
            </div>

            <div class="dashboard-grid">
                
                <!-- Recent Users List -->
                <div class="card">
                    <div class="card-header">
                        <div class="card-title">Recent Users</div>
                        <c:if test="${not empty recentUserList}">
                            <a href="${pageContext.request.contextPath}/admin/users" class="btn btn-outline btn-sm">Manage all</a>
                        </c:if>
                    </div>

                    <c:choose>
                        <c:when test="${not empty recentUserList}">
                            <div class="table-container">
                                <table class="table">
                                    <thead>
                                        <tr>
                                            <th>ID</th>
                                            <th>Name</th>
                                            <th>Email</th>
                                            <th>Role</th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        <c:forEach items="${recentUserList}" var="u">
                                            <tr>
                                                <td class="font-mono text-muted">${u.id}</td>
                                                <td>${u.name}</td>
                                                <td>${u.email}</td>
                                                <td><span class="badge badge-info">${u.role}</span></td>
                                            </tr>
                                        </c:forEach>
                                    </tbody>
                                </table>
                            </div>
                        </c:when>
                        <c:otherwise>
                            <div class="empty-state">
                                <div class="empty-state-title">No registered users found.</div>
                                <div class="empty-state-text">Registered user accounts will appear here.</div>
                            </div>
                        </c:otherwise>
                    </c:choose>
                </div>

                <!-- Pending Feedback Queue -->
                <div class="card">
                    <div class="card-header">
                        <div class="card-title">Feedback Overview</div>
                        <c:if test="${not empty feedbackList}">
                            <a href="${pageContext.request.contextPath}/admin/feedback" class="btn btn-outline btn-sm">View portal</a>
                        </c:if>
                    </div>

                    <c:choose>
                        <c:when test="${not empty feedbackList}">
                            <div class="table-container">
                                <table class="table">
                                    <thead>
                                        <tr>
                                            <th>User ID</th>
                                            <th>Status</th>
                                            <th>Message</th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        <c:forEach items="${feedbackList}" var="fb">
                                            <tr>
                                                <td class="font-mono">${fb.userId != null ? fb.userId : fb.user_id}</td>
                                                <td><span class="badge badge-info">${fb.status}</span></td>
                                                <td>${fb.message}</td>
                                            </tr>
                                        </c:forEach>
                                    </tbody>
                                </table>
                            </div>
                        </c:when>
                        <c:otherwise>
                            <div class="empty-state">
                                <div class="empty-state-title">No feedback tickets available.</div>
                                <div class="empty-state-text">Submitted user feedback will appear here.</div>
                            </div>
                        </c:otherwise>
                    </c:choose>
                </div>

            </div>

            <jsp:include page="../common/parallel-timeline.jsp"/>

        </div>
<jsp:include page="../common/footer.jsp"/>

