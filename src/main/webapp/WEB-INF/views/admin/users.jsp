<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<jsp:include page="../common/header.jsp">
    <jsp:param name="pageTitle" value="Users - Finance."/>
</jsp:include>

<jsp:include page="../common/navbar.jsp">
    <jsp:param name="role" value="ADMIN"/>
</jsp:include>

<div class="app-container">
    <jsp:include page="../common/sidebar.jsp">
        <jsp:param name="activePage" value="admin-users"/>
    </jsp:include>

    <main class="main-content">
        <div class="content-wrapper">
            
            <div class="page-header">
                <div>
                    <h1 class="page-title">Users</h1>
                    <p class="page-subtitle">Registered user accounts.</p>
                </div>
            </div>

            <div class="card">
                <div class="card-header">
                    <div class="card-title">User Accounts</div>
                </div>

                <c:choose>
                    <c:when test="${not empty userList}">
                        <div class="table-container">
                            <table class="table">
                                <thead>
                                    <tr>
                                        <th>User ID</th>
                                        <th>Name</th>
                                        <th>Email</th>
                                        <th>Role</th>
                                        <th class="text-right">Action</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <c:forEach items="${userList}" var="u">
                                        <tr>
                                            <td class="font-mono text-muted">${u.id}</td>
                                            <td><strong>${u.name}</strong></td>
                                            <td>${u.email}</td>
                                            <td><span class="badge badge-info">${u.role}</span></td>
                                            <td class="text-right">
                                                <form method="post" action="${pageContext.request.contextPath}/admin/delete-user" style="display:inline;">
                                                    <input type="hidden" name="id" value="${u.id}">
                                                    <button type="submit" class="btn btn-danger btn-sm btn-delete-confirm" data-item-name="user ${u.name}">Delete</button>
                                                </form>
                                            </td>
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

        </div>
<jsp:include page="../common/footer.jsp"/>

