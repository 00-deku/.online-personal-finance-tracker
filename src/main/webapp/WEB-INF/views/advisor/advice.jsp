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
            
            <c:if test="${not empty errorMessage}">
                <div class="alert alert-error mb-4">
                    ${errorMessage}
                </div>
            </c:if>

            <div class="page-header">
                <div>
                    <h1 class="page-title">Client Advice & Expenses</h1>
                    <p class="page-subtitle">Inspect user expenses and send financial advice.</p>
                </div>
            </div>

            <!-- Select Client User to Inspect -->
            <div class="card mb-4">
                <div class="card-header">
                    <div class="card-title">Select Client User</div>
                </div>
                <form method="get" action="${pageContext.request.contextPath}/advisor/advice" class="flex gap-2 items-center">
                    <select name="selectedUserId" class="form-control" style="max-width: 360px;" onchange="this.form.submit()">
                        <option value="">-- Choose User to View Expenses --</option>
                        <c:forEach items="${userList}" var="u">
                            <option value="${u.id}" ${u.id == selectedUserId ? 'selected' : ''}>
                                ${u.name} (${u.email}) — Role: ${u.role} [ID: ${u.id}]
                            </option>
                        </c:forEach>
                    </select>
                    <button type="submit" class="btn btn-secondary">Inspect Expenses</button>
                </form>
            </div>

            <!-- Selected User Expense View -->
            <c:if test="${not empty selectedUserId}">
                <div class="card mb-4">
                    <div class="card-header">
                        <div class="card-title">Expenses for User: <span class="font-mono">${selectedUserId}</span></div>
                    </div>
                    <c:choose>
                        <c:when test="${not empty selectedUserExpenses}">
                            <div class="table-container">
                                <table class="table">
                                    <thead>
                                        <tr>
                                            <th>ID</th>
                                            <th>Category</th>
                                            <th>Date</th>
                                            <th class="text-right">Amount</th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        <c:forEach items="${selectedUserExpenses}" var="exp">
                                            <tr>
                                                <td class="font-mono text-muted">${exp.id}</td>
                                                <td>${exp.category}</td>
                                                <td class="text-muted">${exp.date}</td>
                                                <td class="text-right font-mono">₹${exp.amount}</td>
                                            </tr>
                                        </c:forEach>
                                    </tbody>
                                </table>
                            </div>
                        </c:when>
                        <c:otherwise>
                            <div class="empty-state">
                                <div class="empty-state-title">No expenses recorded for this user.</div>
                                <div class="empty-state-text">The selected user has not logged any expenses yet.</div>
                            </div>
                        </c:otherwise>
                    </c:choose>
                </div>
            </c:if>

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
                                   placeholder="User ID" value="${selectedUserId != null ? selectedUserId : ''}" maxlength="30" required>
                            <span class="form-error">Target User ID is required.</span>
                        </div>

                        <div class="form-group">
                            <label for="date" class="form-label">Date</label>
                            <input type="date" id="date" name="date" class="form-control" required>
                            <span class="form-error">Date is required.</span>
                        </div>

                        <div class="form-group">
                            <label for="message" class="form-label">Advice Message</label>
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
                                                <td class="font-mono">${adv.userId != null ? adv.userId : adv.user_id}</td>
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
                                <div class="empty-state-title">No advice records issued yet.</div>
                                <div class="empty-state-text">Recommendations issued to client users will appear here.</div>
                            </div>
                        </c:otherwise>
                    </c:choose>
                </div>

            </div>

        </div>
<jsp:include page="../common/footer.jsp"/>

