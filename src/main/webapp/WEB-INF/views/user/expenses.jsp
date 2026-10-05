<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<jsp:include page="../common/header.jsp">
    <jsp:param name="pageTitle" value="Expenses - Finance."/>
</jsp:include>

<jsp:include page="../common/navbar.jsp">
    <jsp:param name="role" value="USER"/>
</jsp:include>

<div class="app-container">
    <jsp:include page="../common/sidebar.jsp">
        <jsp:param name="activePage" value="expenses"/>
    </jsp:include>

    <main class="main-content">
        <div class="content-wrapper">
            
            <div class="page-header">
                <div>
                    <h1 class="page-title">Expenses</h1>
                    <p class="page-subtitle">Track and manage your daily transactions.</p>
                </div>
                <div>
                    <a href="${pageContext.request.contextPath}/user/add-expense" class="btn btn-primary">+ Add Expense</a>
                </div>
            </div>

            <!-- Expense List Table -->
            <div class="card">
                <div class="card-header">
                    <div class="card-title">Recorded Transactions</div>
                </div>

                <c:choose>
                    <c:when test="${not empty expensesList}">
                        <div class="table-container">
                            <table class="table">
                                <thead>
                                    <tr>
                                        <th>ID</th>
                                        <th>Category</th>
                                        <th>Date</th>
                                        <th class="text-right">Amount</th>
                                        <th class="text-right">Actions</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <c:forEach items="${expensesList}" var="expense">
                                        <tr>
                                            <td class="font-mono text-muted">${expense.id}</td>
                                            <td><strong>${expense.category}</strong></td>
                                            <td class="text-muted">${expense.date}</td>
                                            <td class="text-right font-mono">₹${expense.amount}</td>
                                            <td class="text-right">
                                                <div class="flex gap-1" style="justify-content: flex-end;">
                                                    <a href="${pageContext.request.contextPath}/user/add-expense?id=${expense.id}" class="btn btn-secondary btn-sm">Edit</a>
                                                    <form method="post" action="${pageContext.request.contextPath}/user/delete-expense" style="display:inline;">
                                                        <input type="hidden" name="id" value="${expense.id}">
                                                        <button type="submit" class="btn btn-danger btn-sm btn-delete-confirm" data-item-name="expense ID ${expense.id}">Delete</button>
                                                    </form>
                                                </div>
                                            </td>
                                        </tr>
                                    </c:forEach>
                                </tbody>
                            </table>
                        </div>
                    </c:when>
                    <c:otherwise>
                        <div class="empty-state">
                            <div class="empty-state-title">No expenses yet.</div>
                            <div class="empty-state-text">Add your first expense to start tracking your spending.</div>
                        </div>
                    </c:otherwise>
                </c:choose>

            </div>

        </div>
    </main>
</div>

<jsp:include page="../common/footer.jsp"/>
