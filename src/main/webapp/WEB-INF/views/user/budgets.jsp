<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<jsp:include page="../common/header.jsp">
    <jsp:param name="pageTitle" value="Budgets - Finance."/>
</jsp:include>

<jsp:include page="../common/navbar.jsp">
    <jsp:param name="role" value="USER"/>
</jsp:include>

<div class="app-container">
    <jsp:include page="../common/sidebar.jsp">
        <jsp:param name="activePage" value="budgets"/>
    </jsp:include>

    <main class="main-content">
        <div class="content-wrapper">
            
            <div class="page-header">
                <div>
                    <h1 class="page-title">Budgets</h1>
                    <p class="page-subtitle">Set category spending limits and monitor allocation.</p>
                </div>
            </div>

            <div class="dashboard-grid">
                
                <!-- Left: Budgets List -->
                <div>
                    <div class="card">
                        <div class="card-header">
                            <div class="card-title">Category Budgets</div>
                        </div>

                        <c:choose>
                            <c:when test="${not empty budgetsList}">
                                <c:forEach items="${budgetsList}" var="b">
                                    <div class="card mb-3" style="background-color: var(--bg-input);" data-budget-amount="${b.amount}" data-budget-spent="${b.spent}">
                                        <div class="flex-between mb-1">
                                            <div>
                                                <strong>${b.category}</strong>
                                            </div>
                                            <span class="badge badge-info">${b.period}</span>
                                        </div>
                                        <div class="text-secondary" style="font-size: 0.85rem;">
                                            Budget: <strong class="font-mono">₹${b.amount}</strong> | Spent: <strong class="font-mono">₹${b.spent != null ? b.spent : '0.00'}</strong>
                                        </div>
                                        <div class="progress-container">
                                            <div class="progress-bar"></div>
                                        </div>
                                        <div class="flex-between mt-2" style="font-size: 0.8rem; color: var(--text-muted);">
                                            <span class="budget-remaining">Remaining: —</span>
                                            <form method="post" action="${pageContext.request.contextPath}/user/delete-budget" style="display:inline;">
                                                <input type="hidden" name="id" value="${b.id}">
                                                <button type="submit" class="btn btn-danger btn-sm btn-delete-confirm" data-item-name="budget">Delete</button>
                                            </form>
                                        </div>
                                    </div>
                                </c:forEach>
                            </c:when>
                            <c:otherwise>
                                <div class="empty-state">
                                    <div class="empty-state-title">No budgets available.</div>
                                    <div class="empty-state-text">Create a budget to start managing your spending limits.</div>
                                </div>
                            </c:otherwise>
                        </c:choose>

                    </div>
                </div>

                <!-- Right: Set Budget Form -->
                <div>
                    <div class="card">
                        <div class="card-header">
                            <div class="card-title">Set Budget</div>
                        </div>

                        <form id="budgetForm" method="post" action="${pageContext.request.contextPath}/user/add-budget" novalidate>
                            
                            <div class="form-group">
                                <label for="category" class="form-label">Category</label>
                                <input type="text" id="category" name="category" class="form-control" 
                                       placeholder="e.g. Groceries, Utilities" maxlength="20" required>
                                <span class="form-hint">Maximum 20 characters</span>
                                <span class="form-error">Category is required (max 20 characters).</span>
                            </div>

                            <div class="form-group">
                                <label for="amount" class="form-label">Amount (₹)</label>
                                <input type="number" id="amount" name="amount" class="form-control" 
                                       placeholder="0.00" step="0.01" min="0.01" required>
                                <span class="form-error">Please enter a valid target budget amount.</span>
                            </div>

                            <div class="form-group">
                                <label for="period" class="form-label">Period</label>
                                <select id="period" name="period" class="form-control" required>
                                    <option value="Monthly" selected>Monthly</option>
                                    <option value="Weekly">Weekly</option>
                                    <option value="Annual">Annual</option>
                                </select>
                                <span class="form-error">Period is required.</span>
                            </div>

                            <button type="submit" class="btn btn-primary btn-block mt-3">Save Budget</button>
                        </form>
                    </div>
                </div>

            </div>

        </div>
<jsp:include page="../common/footer.jsp"/>

