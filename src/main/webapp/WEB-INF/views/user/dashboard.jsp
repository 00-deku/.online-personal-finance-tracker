<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<jsp:include page="../common/header.jsp">
    <jsp:param name="pageTitle" value="Dashboard - Finance."/>
</jsp:include>

<jsp:include page="../common/navbar.jsp">
    <jsp:param name="role" value="USER"/>
</jsp:include>

<div class="app-container">
    <jsp:include page="../common/sidebar.jsp">
        <jsp:param name="activePage" value="dashboard"/>
    </jsp:include>

    <main class="main-content">
        <div class="content-wrapper">
            
            <!-- Dashboard Heading -->
            <div class="page-header">
                <div>
                    <h1 class="page-title">Dashboard</h1>
                    <p class="page-subtitle">Welcome back<c:if test="${not empty sessionScope.user.name}">, ${sessionScope.user.name}</c:if>.</p>
                </div>
                <div class="flex gap-2">
                    <a href="${pageContext.request.contextPath}/user/add-expense" class="btn btn-primary btn-sm">+ Add Expense</a>
                    <a href="${pageContext.request.contextPath}/user/budgets" class="btn btn-secondary btn-sm">Set Budget</a>
                </div>
            </div>

            <!-- Financial Summary Cards (Backend Data Driven) -->
            <div class="stats-grid">
                <div class="stat-card">
                    <div class="stat-label">Total Expenses</div>
                    <div class="stat-value">
                        <c:choose>
                            <c:if test="${not empty totalExpenses}">₹${totalExpenses}</c:if>
                            <c:otherwise>—</c:otherwise>
                        </c:choose>
                    </div>
                    <div class="stat-subtext">Sum of recorded expenses</div>
                </div>

                <div class="stat-card">
                    <div class="stat-label">Current Budget</div>
                    <div class="stat-value">
                        <c:choose>
                            <c:if test="${not empty totalBudget}">₹${totalBudget}</c:if>
                            <c:otherwise>—</c:otherwise>
                        </c:choose>
                    </div>
                    <div class="stat-subtext">Active limit</div>
                </div>

                <div class="stat-card">
                    <div class="stat-label">Remaining Budget</div>
                    <div class="stat-value">
                        <c:choose>
                            <c:if test="${not empty remainingBudget}">₹${remainingBudget}</c:if>
                            <c:otherwise>—</c:otherwise>
                        </c:choose>
                    </div>
                    <div class="stat-subtext">Available buffer</div>
                </div>
            </div>

            <!-- Dashboard Grid -->
            <div class="dashboard-grid">
                
                <!-- Left: Recent Expenses & Category Summary -->
                <div class="flex flex-column gap-4">
                    
                    <!-- Recent Expenses Section -->
                    <div class="card">
                        <div class="card-header">
                            <div class="card-title">Recent Expenses</div>
                            <c:if test="${not empty recentExpenses}">
                                <a href="${pageContext.request.contextPath}/user/expenses" class="btn btn-outline btn-sm">View all</a>
                            </c:if>
                        </div>
                        
                        <c:choose>
                            <c:when test="${not empty recentExpenses}">
                                <div class="table-container">
                                    <table class="table">
                                        <thead>
                                            <tr>
                                                <th>Category</th>
                                                <th>Date</th>
                                                <th class="text-right">Amount</th>
                                            </tr>
                                        </thead>
                                        <tbody>
                                            <c:forEach items="${recentExpenses}" var="expense">
                                                <tr>
                                                    <td>${expense.category}</td>
                                                    <td class="text-muted">${expense.date}</td>
                                                    <td class="text-right font-mono">₹${expense.amount}</td>
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

                    <!-- Category Breakdown Section -->
                    <div class="card">
                        <div class="card-header">
                            <div class="card-title">Spending by Category</div>
                        </div>

                        <c:choose>
                            <c:when test="${not empty categoryBreakdown}">
                                <div class="chart-bar-group">
                                    <c:forEach items="${categoryBreakdown}" var="cat">
                                        <div class="chart-bar-row">
                                            <div class="chart-bar-label">
                                                <span>${cat.category}</span>
                                                <span class="font-mono">₹${cat.amount}</span>
                                            </div>
                                            <div class="chart-bar-bg">
                                                <div class="chart-bar-fill" style="width: ${cat.percentage}%;"></div>
                                            </div>
                                        </div>
                                    </c:forEach>
                                </div>
                            </c:when>
                            <c:otherwise>
                                <div class="empty-state">
                                    <div class="empty-state-title">No spending data available.</div>
                                    <div class="empty-state-text">Category breakdown will appear once expenses are logged.</div>
                                </div>
                            </c:otherwise>
                        </c:choose>
                    </div>

                </div>

                <!-- Right: Budget Status & Advisor Advice Feed -->
                <div class="flex flex-column gap-4">
                    
                    <!-- Budget Status Card -->
                    <div class="card">
                        <div class="card-header">
                            <div class="card-title">Budget Status</div>
                        </div>

                        <c:choose>
                            <c:when test="${not empty activeBudget}">
                                <p style="font-size: 0.85rem;" class="mb-2">
                                    Spent <strong class="font-mono">₹${activeBudget.spent}</strong> of <strong class="font-mono">₹${activeBudget.amount}</strong>
                                </p>
                                <div class="progress-container">
                                    <div class="progress-bar" style="width: ${activeBudget.percentage}%;"></div>
                                </div>
                            </c:when>
                            <c:otherwise>
                                <div class="empty-state">
                                    <div class="empty-state-title">No budgets available.</div>
                                    <div class="empty-state-text">Create a budget to start managing your spending limits.</div>
                                </div>
                            </c:otherwise>
                        </c:choose>
                    </div>

                    <!-- Advisor Guidance Feed -->
                    <div class="card">
                        <div class="card-header">
                            <div class="card-title">Advisor Recommendations</div>
                        </div>

                        <c:choose>
                            <c:when test="${not empty adviceList}">
                                <c:forEach items="${adviceList}" var="adv">
                                    <div class="advice-card">
                                        <div class="advice-meta">
                                            <span>Advisor ID: ${adv.advisor_id}</span>
                                            <span>${adv.date}</span>
                                        </div>
                                        <div class="advice-content">${adv.message}</div>
                                    </div>
                                </c:forEach>
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

        </div>
    </main>
</div>

<jsp:include page="../common/footer.jsp"/>
