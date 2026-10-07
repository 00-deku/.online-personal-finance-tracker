<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<header class="app-navbar">
    <div class="flex items-center gap-2">
        <button type="button" class="mobile-nav-toggle" id="mobileNavToggle" aria-label="Toggle menu">
            &#9776;
        </button>
        <a href="${pageContext.request.contextPath}/" class="navbar-brand">
            Finance.
        </a>
    </div>

    <div class="navbar-actions">
        <a href="${pageContext.request.contextPath}/docs" class="navbar-link">Docs</a>

        <%-- Display session user details if logged in --%>
        <div class="user-profile-badge">
            <span>${sessionScope.user != null ? sessionScope.user.name : (param.role != null ? param.role : 'Account')}</span>
            <span class="role-pill">${sessionScope.user != null ? sessionScope.user.role : (param.role != null ? param.role : 'USER')}</span>
        </div>

        <form method="post" action="${pageContext.request.contextPath}/logout" style="margin: 0;">
            <button type="submit" class="btn btn-outline btn-sm">Log out</button>
        </form>
    </div>
</header>
