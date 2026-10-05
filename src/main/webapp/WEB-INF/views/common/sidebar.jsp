<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<div class="sidebar-overlay" id="sidebarOverlay"></div>

<aside class="app-sidebar" id="appSidebar">
    
    <div class="sidebar-brand">
        Finance.
    </div>

    <!-- User Core Navigation -->
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
                <span>Profile</span>
            </a>
        </li>
    </ul>

    <!-- Advisor Navigation (rendered when role is ADVISOR or for testing) -->
    <div class="sidebar-heading">Advisor</div>
    <ul class="sidebar-menu mb-3">
        <li class="sidebar-item ${param.activePage == 'advisor-dashboard' ? 'active' : ''}">
            <a href="${pageContext.request.contextPath}/advisor/dashboard">
                <span>Advisor Overview</span>
            </a>
        </li>
        <li class="sidebar-item ${param.activePage == 'advisor-advice' ? 'active' : ''}">
            <a href="${pageContext.request.contextPath}/advisor/advice">
                <span>Advice Management</span>
            </a>
        </li>
    </ul>

    <!-- Admin Navigation (rendered when role is ADMIN or for testing) -->
    <div class="sidebar-heading">Admin</div>
    <ul class="sidebar-menu mb-3">
        <li class="sidebar-item ${param.activePage == 'admin-dashboard' ? 'active' : ''}">
            <a href="${pageContext.request.contextPath}/admin/dashboard">
                <span>Admin Dashboard</span>
            </a>
        </li>
        <li class="sidebar-item ${param.activePage == 'admin-users' ? 'active' : ''}">
            <a href="${pageContext.request.contextPath}/admin/users">
                <span>Users</span>
            </a>
        </li>
        <li class="sidebar-item ${param.activePage == 'admin-feedback' ? 'active' : ''}">
            <a href="${pageContext.request.contextPath}/admin/feedback">
                <span>Feedback</span>
            </a>
        </li>
    </ul>

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
