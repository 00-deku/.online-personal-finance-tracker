<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<div class="sidebar-overlay" id="sidebarOverlay"></div>

<aside class="app-sidebar" id="appSidebar">
    
    <div class="sidebar-brand">
        Finance.
    </div>

    <c:set var="userRole" value="${sessionScope.user != null ? sessionScope.user.role : 'USER'}"/>

    <c:choose>
        <%-- ADVISOR MENU --%>
        <c:when test="${userRole == 'ADVISOR'}">
            <div class="sidebar-heading">Advisor Menu</div>
            <ul class="sidebar-menu mb-3">
                <li class="sidebar-item ${param.activePage == 'advisor-dashboard' ? 'active' : ''}">
                    <a href="${pageContext.request.contextPath}/advisor/dashboard">
                        <span>Advisor Overview</span>
                    </a>
                </li>
                <li class="sidebar-item ${param.activePage == 'advisor-advice' ? 'active' : ''}">
                    <a href="${pageContext.request.contextPath}/advisor/advice">
                        <span>Client Advice & Expenses</span>
                    </a>
                </li>
            </ul>
        </c:when>

        <%-- ADMIN MENU --%>
        <c:when test="${userRole == 'ADMIN'}">
            <div class="sidebar-heading">Admin Menu</div>
            <ul class="sidebar-menu mb-3">
                <li class="sidebar-item ${param.activePage == 'admin-dashboard' ? 'active' : ''}">
                    <a href="${pageContext.request.contextPath}/admin/dashboard">
                        <span>Admin Overview</span>
                    </a>
                </li>
                <li class="sidebar-item ${param.activePage == 'admin-users' ? 'active' : ''}">
                    <a href="${pageContext.request.contextPath}/admin/users">
                        <span>User Accounts</span>
                    </a>
                </li>
                <li class="sidebar-item ${param.activePage == 'admin-feedback' ? 'active' : ''}">
                    <a href="${pageContext.request.contextPath}/admin/feedback">
                        <span>System Feedback</span>
                    </a>
                </li>
            </ul>
        </c:when>

        <%-- USER MENU (DEFAULT) --%>
        <c:otherwise>
            <div class="sidebar-heading">Personal Finance</div>
            <ul class="sidebar-menu mb-3">
                <li class="sidebar-item ${param.activePage == 'dashboard' ? 'active' : ''}">
                    <a href="${pageContext.request.contextPath}/user/dashboard">
                        <span>Dashboard</span>
                    </a>
                </li>
                <li class="sidebar-item ${param.activePage == 'expenses' ? 'active' : ''}">
                    <a href="${pageContext.request.contextPath}/user/expenses">
                        <span>Expenses</span>
                    </a>
                </li>
                <li class="sidebar-item ${param.activePage == 'budgets' ? 'active' : ''}">
                    <a href="${pageContext.request.contextPath}/user/budgets">
                        <span>Budgets</span>
                    </a>
                </li>
                <li class="sidebar-item ${param.activePage == 'reports' ? 'active' : ''}">
                    <a href="${pageContext.request.contextPath}/user/reports">
                        <span>Reports</span>
                    </a>
                </li>
                <li class="sidebar-item ${param.activePage == 'profile' ? 'active' : ''}">
                    <a href="${pageContext.request.contextPath}/user/profile">
                        <span>Profile & Advice</span>
                    </a>
                </li>
            </ul>
        </c:otherwise>
    </c:choose>

    <!-- Logout Link -->
    <ul class="sidebar-menu" style="margin-top: auto;">
        <li class="sidebar-item">
            <form method="post" action="${pageContext.request.contextPath}/logout" style="margin: 0; width: 100%;">
                <button type="submit" style="background: none; border: none; width: 100%; text-align: left; padding: 8px 12px; color: var(--text-secondary); cursor: pointer; font-family: inherit; font-size: 0.85rem;">
                    Log out
                </button>
            </form>
        </li>
    </ul>

</aside>
