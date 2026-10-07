<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<jsp:include page="../common/header.jsp">
    <jsp:param name="pageTitle" value="Advisor Dashboard - Finance."/>
</jsp:include>

<jsp:include page="../common/navbar.jsp">
    <jsp:param name="role" value="ADVISOR"/>
</jsp:include>

<div class="app-container">
    <jsp:include page="../common/sidebar.jsp">
        <jsp:param name="activePage" value="advisor-dashboard"/>
    </jsp:include>

    <main class="main-content">
        <div class="content-wrapper">
            
            <div class="page-header">
                <div>
                    <h1 class="page-title">Advisor Dashboard</h1>
                    <p class="page-subtitle">Manage client recommendations.</p>
                </div>
                <a href="${pageContext.request.contextPath}/advisor/advice" class="btn btn-primary">+ Issue Advice</a>
            </div>

            <div class="stats-grid mb-4">
                <div class="stat-card">
                    <div class="stat-label">Advice Issued</div>
                    <div class="stat-value"><c:choose><c:when test="${not empty adviceCount}">${adviceCount}</c:when><c:otherwise>—</c:otherwise></c:choose></div>
                    <div class="stat-subtext">Total recommendations</div>
                </div>

                <div class="stat-card">
                    <div class="stat-label">Advised Users</div>
                    <div class="stat-value"><c:choose><c:when test="${not empty uniqueUsersAdvised}">${uniqueUsersAdvised}</c:when><c:otherwise>—</c:otherwise></c:choose></div>
                    <div class="stat-subtext">Client count</div>
                </div>
            </div>

            <div class="card">
                <div class="card-header">
                    <div class="card-title">Recent Issued Advice</div>
                </div>

                <c:choose>
                    <c:when test="${not empty adviceList}">
                        <div class="table-container">
                            <table class="table">
                                <thead>
                                    <tr>
                                        <th>ID</th>
                                        <th>Target User ID</th>
                                        <th>Date</th>
                                        <th>Message</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <c:forEach items="${adviceList}" var="adv">
                                        <tr>
                                            <td class="font-mono text-muted">${adv.id}</td>
                                            <td class="font-mono">${adv.user_id}</td>
                                            <td class="text-muted">${adv.date}</td>
                                            <td>${adv.message}</td>
                                        </tr>
                                    </c:forEach>
                                </tbody>
                            </table>
                        </div>
                    </c:when>
                    <c:otherwise>
                        <div class="empty-state">
                            <div class="empty-state-title">No advice records available.</div>
                            <div class="empty-state-text">Recommendations issued to users will appear here.</div>
                        </div>
                    </c:otherwise>
                </c:choose>

            </div>

            <jsp:include page="../common/parallel-timeline.jsp"/>

        </div>
<jsp:include page="../common/footer.jsp"/>

