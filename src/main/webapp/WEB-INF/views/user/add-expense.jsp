<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<jsp:include page="../common/header.jsp">
    <jsp:param name="pageTitle" value="Add Expense - Finance."/>
</jsp:include>

<jsp:include page="../common/navbar.jsp">
    <jsp:param name="role" value="USER"/>
</jsp:include>

<div class="app-container">
    <jsp:include page="../common/sidebar.jsp">
        <jsp:param name="activePage" value="add-expense"/>
    </jsp:include>

    <main class="main-content">
        <div class="content-wrapper" style="max-width: 560px;">
            
            <div class="page-header">
                <div>
                    <h1 class="page-title"><c:choose><c:when test="${not empty expense}">Edit Expense</c:when><c:otherwise>Add Expense</c:otherwise></c:choose></h1>
                    <p class="page-subtitle">Enter expense details.</p>
                </div>
                <a href="${pageContext.request.contextPath}/user/expenses" class="btn btn-secondary btn-sm">Back</a>
            </div>

            <div class="card">
                <form id="expenseForm" method="post" action="${pageContext.request.contextPath}/user/add-expense" novalidate>
                    
                    <input type="hidden" name="id" value="${expense != null ? expense.id : param.id}">

                    <div class="form-group">
                        <label for="category" class="form-label">Category</label>
                        <input type="text" id="category" name="category" class="form-control" 
                               placeholder="e.g. Groceries, Rent, Utilities" maxlength="15" 
                               value="${expense != null ? expense.category : ''}" required>
                        <span class="form-hint">Maximum 15 characters</span>
                        <span class="form-error">Category is required (max 15 characters).</span>
                    </div>

                    <div class="form-group">
                        <label for="amount" class="form-label">Amount (₹)</label>
                        <input type="number" id="amount" name="amount" class="form-control" 
                               placeholder="0.00" step="0.01" min="0.01" 
                               value="${expense != null ? expense.amount : ''}" required>
                        <span class="form-error">Please enter a valid positive amount.</span>
                    </div>

                    <div class="form-group">
                        <label for="date" class="form-label">Date</label>
                        <input type="date" id="date" name="date" class="form-control" 
                               value="${expense != null ? expense.date : ''}" required>
                        <span class="form-error">Date is required.</span>
                    </div>

                    <div class="flex gap-2 mt-4">
                        <button type="submit" class="btn btn-primary">Save Expense</button>
                        <a href="${pageContext.request.contextPath}/user/expenses" class="btn btn-secondary">Cancel</a>
                    </div>
                </form>
            </div>

        </div>
<jsp:include page="../common/footer.jsp"/>

