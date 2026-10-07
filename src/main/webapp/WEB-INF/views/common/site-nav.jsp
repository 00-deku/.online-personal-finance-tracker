<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%-- Public navbar used by the landing page and docs. Section links point at the landing page anchors. --%>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<c:set var="homeUrl" value=""/>
<c:if test="${param.onLanding != 'true'}"><c:set var="homeUrl" value="${ctx}/"/></c:if>
<c:choose>
    <c:when test="${sessionScope.user.role == 'ADMIN'}"><c:set var="dashUrl" value="${ctx}/admin/dashboard"/></c:when>
    <c:when test="${sessionScope.user.role == 'ADVISOR'}"><c:set var="dashUrl" value="${ctx}/advisor/dashboard"/></c:when>
    <c:otherwise><c:set var="dashUrl" value="${ctx}/user/dashboard"/></c:otherwise>
</c:choose>

<header class="app-navbar">
    <a href="${ctx}/" class="navbar-brand">Finance.</a>

    <nav class="site-nav-links" aria-label="Main">
        <a href="${homeUrl}#features" class="navbar-link nav-section-link">Features</a>
        <a href="${homeUrl}#parallel" class="navbar-link nav-section-link">Multithreading</a>
        <a href="${homeUrl}#how-it-works" class="navbar-link nav-section-link">How it works</a>
        <a href="${ctx}/docs" class="navbar-link ${param.active == 'docs' ? 'active' : ''}">Docs</a>
        <c:choose>
            <c:when test="${not empty sessionScope.user}">
                <a href="${dashUrl}" class="btn btn-primary btn-sm">Open dashboard</a>
            </c:when>
            <c:otherwise>
                <a href="${ctx}/login" class="navbar-link">Sign in</a>
                <a href="${ctx}/register" class="btn btn-primary btn-sm">Get started</a>
            </c:otherwise>
        </c:choose>
    </nav>
</header>
