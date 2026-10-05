<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<jsp:include page="../common/header.jsp">
    <jsp:param name="pageTitle" value="Feedback - Finance."/>
</jsp:include>

<jsp:include page="../common/navbar.jsp">
    <jsp:param name="role" value="ADMIN"/>
</jsp:include>

<div class="app-container">
    <jsp:include page="../common/sidebar.jsp">
        <jsp:param name="activePage" value="admin-feedback"/>
    </jsp:include>

    <main class="main-content">
        <div class="content-wrapper">
            
            <div class="page-header">
                <div>
                    <h1 class="page-title">Feedback</h1>
                    <p class="page-subtitle">User feedback and support tickets.</p>
                </div>
            </div>

            <div class="card">
                <div class="card-header">
                    <div class="card-title">Feedback Queue</div>
                </div>

                <c:choose>
                    <c:when test="${not empty feedbackList}">
                        <div class="table-container">
                            <table class="table">
                                <thead>
                                    <tr>
                                        <th>Feedback ID</th>
                                        <th>User ID</th>
                                        <th>Date</th>
                                        <th>Message</th>
                                        <th>Status</th>
                                        <th class="text-right">Update Status</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <c:forEach items="${feedbackList}" var="fb">
                                        <tr>
                                            <td class="font-mono text-muted">${fb.id}</td>
                                            <td class="font-mono">${fb.user_id}</td>
                                            <td class="text-muted">${fb.date}</td>
                                            <td>${fb.message}</td>
                                            <td>
                                                <span class="badge badge-info">${fb.status}</span>
                                            </td>
                                            <td class="text-right">
                                                <form method="post" action="${pageContext.request.contextPath}/admin/update-feedback-status" class="flex gap-1" style="justify-content: flex-end;">
                                                    <input type="hidden" name="id" value="${fb.id}">
                                                    <select name="status" class="form-control btn-sm" style="width: 110px;">
                                                        <option value="PENDING" ${fb.status == 'PENDING' ? 'selected' : ''}>PENDING</option>
                                                        <option value="IN_REVIEW" ${fb.status == 'IN_REVIEW' ? 'selected' : ''}>IN_REVIEW</option>
                                                        <option value="RESOLVED" ${fb.status == 'RESOLVED' ? 'selected' : ''}>RESOLVED</option>
                                                    </select>
                                                    <button type="submit" class="btn btn-secondary btn-sm">Save</button>
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
                            <div class="empty-state-title">No feedback tickets available.</div>
                            <div class="empty-state-text">Submitted user feedback will appear here.</div>
                        </div>
                    </c:otherwise>
                </c:choose>

            </div>

        </div>
    </main>
</div>

<jsp:include page="../common/footer.jsp"/>
