<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<jsp:include page="../common/header.jsp">
    <jsp:param name="pageTitle" value="Advice Management - Finance."/>
</jsp:include>

<jsp:include page="../common/navbar.jsp">
    <jsp:param name="role" value="ADVISOR"/>
</jsp:include>

<div class="app-container">
    <jsp:include page="../common/sidebar.jsp">
        <jsp:param name="activePage" value="advisor-advice"/>
    </jsp:include>

    <main class="main-content">
        <div class="content-wrapper">
            
            <div class="page-header">
                <div>
                    <h1 class="page-title">Advice Management</h1>
                    <p class="page-subtitle">Send financial advice to target users.</p>
                </div>
            </div>

            <div class="dashboard-grid">
                
                <!-- Send Advice Form -->
                <div class="card">
                    <div class="card-header">
                        <div class="card-title">Send Advice</div>
                    </div>

                    <form id="adviceForm" method="post" action="${pageContext.request.contextPath}/advisor/send-advice" novalidate>
                        
                        <div class="form-group">
                            <label for="advisor_id" class="form-label">Advisor ID</label>
                            <input type="text" id="advisor_id" name="advisor_id" class="form-control" 
                                   value="${sessionScope.user.id}" maxlength="30" required readonly>
                        </div>

                        <div class="form-group">
                            <label for="user_id" class="form-label">Target User ID</label>
                            <input type="text" id="user_id" name="user_id" class="form-control" 
                                   placeholder="User ID" maxlength="30" required>
                            <span class="form-error">Target User ID is required.</span>
                        </div>

                        <div class="form-group">
                            <label for="date" class="form-label">Date</label>
                            <input type="date" id="date" name="date" class="form-control" required>
                            <span class="form-error">Date is required.</span>
                        </div>

                        <div class="form-group">
                            <label for="message" class="form-label">Message</label>
                            <textarea id="message" name="message" class="form-control" 
                                      placeholder="Write advice message..." maxlength="200" required></textarea>
                            <span class="form-hint">Maximum 200 characters</span>
                            <span class="form-error">Advice message cannot be empty (max 200 chars).</span>
                        </div>

                        <button type="submit" class="btn btn-primary btn-block mt-3">Send Advice</button>
                    </form>
                </div>

                <!-- Issued Advice List -->
                <div class="card">
                    <div class="card-header">
                        <div class="card-title">Issued Advice</div>
                    </div>

                    <c:choose>
                        <c:when test="${not empty adviceList}">
                            <div class="table-container">
                                <table class="table">
                                    <thead>
                                        <tr>
                                            <th>Target User</th>
                                            <th>Date</th>
                                            <th>Message</th>
                                            <th class="text-right">Action</th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        <c:forEach items="${adviceList}" var="adv">
                                            <tr>
                                                <td class="font-mono">${adv.user_id}</td>
                                                <td class="text-muted">${adv.date}</td>
                                                <td>${adv.message}</td>
                                                <td class="text-right">
                                                    <form method="post" action="${pageContext.request.contextPath}/advisor/delete-advice" style="display:inline;">
                                                        <input type="hidden" name="id" value="${adv.id}">
                                                        <button type="submit" class="btn btn-danger btn-sm btn-delete-confirm" data-item-name="advice">Delete</button>
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
                                <div class="empty-state-title">No advice available.</div>
                                <div class="empty-state-text">Your advisor hasn't added any advice yet.</div>
                            </div>
                        </c:otherwise>
                    </c:choose>
                </div>

            </div>

        </div>
    </main>
</div>

<jsp:include page="../common/footer.jsp"/>
