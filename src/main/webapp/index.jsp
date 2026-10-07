<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta name="description" content="Track expenses, set budgets, get advisor recommendations and generate reports — a personal finance tracker built with Java Servlets and JSP.">
    <title>Finance. - Personal Finance Tracker</title>

    <link rel="stylesheet" href="${ctx}/assets/css/style.css">
    <link rel="stylesheet" href="${ctx}/assets/css/dashboard.css">
    <link rel="stylesheet" href="${ctx}/assets/css/landing.css">
    <link rel="stylesheet" href="${ctx}/assets/css/responsive.css">
    <link rel="shortcut icon" href="${ctx}/assets/images/favicon.ico" type="image/x-icon">
</head>
<body>

<jsp:include page="/WEB-INF/views/common/site-nav.jsp">
    <jsp:param name="onLanding" value="true"/>
</jsp:include>

<main class="site-main">

    <!-- ============ HERO ============ -->
    <section class="section hero">
        <div>
            <div class="hero-pill"><span class="hero-pill-dot"></span>Java Servlets &middot; JSP &middot; MySQL &middot; Multithreaded</div>
            <h1 class="hero-title">Manage your money<br><span>with clarity.</span></h1>
            <p class="hero-text">
                A calm, focused personal finance tracker. Record daily expenses, set category budgets,
                get recommendations from a financial advisor, and turn it all into clear reports.
            </p>

            <div class="hero-actions">
                <c:choose>
                    <c:when test="${not empty sessionScope.user}">
                        <a href="${ctx}/login" class="btn btn-primary">Open dashboard</a>
                    </c:when>
                    <c:otherwise>
                        <a href="${ctx}/register" class="btn btn-primary">Get started free</a>
                        <a href="${ctx}/login" class="btn btn-secondary">Sign in</a>
                    </c:otherwise>
                </c:choose>
                <a href="${ctx}/docs" class="btn btn-outline">Read the docs</a>
            </div>

            <div class="hero-meta">
                <div class="hero-meta-item"><strong>3</strong>roles</div>
                <div class="hero-meta-item"><strong>7</strong>queries in parallel per dashboard</div>
                <div class="hero-meta-item"><strong>2-phase</strong>parallel reports</div>
            </div>
        </div>

        <!-- Preview built from the same components as the real dashboard -->
        <div class="preview-window" aria-hidden="true">
            <div class="preview-bar">
                <i></i><i></i><i></i>
                <span>/finance/user/dashboard</span>
            </div>
            <div class="preview-body">
                <div class="preview-side">
                    <div class="active">Dashboard</div>
                    <div>Expenses</div>
                    <div>Budgets</div>
                    <div>Reports</div>
                    <div>Profile</div>
                </div>
                <div class="preview-content">
                    <div class="preview-stats">
                        <div class="stat-card">
                            <div class="stat-label">Expenses</div>
                            <div class="stat-value">₹18,420</div>
                        </div>
                        <div class="stat-card">
                            <div class="stat-label">Budget</div>
                            <div class="stat-value">₹25,000</div>
                        </div>
                        <div class="stat-card">
                            <div class="stat-label">Remaining</div>
                            <div class="stat-value">₹6,580</div>
                        </div>
                    </div>

                    <div class="card">
                        <div class="card-title mb-2" style="font-size: 0.82rem;">Spending by Category</div>
                        <div class="chart-bar-group">
                            <div class="chart-bar-row">
                                <div class="chart-bar-label"><span>Rent</span><span class="font-mono">₹9,000</span></div>
                                <div class="chart-bar-bg"><div class="chart-bar-fill" style="width: 49%;"></div></div>
                            </div>
                            <div class="chart-bar-row">
                                <div class="chart-bar-label"><span>Groceries</span><span class="font-mono">₹4,850</span></div>
                                <div class="chart-bar-bg"><div class="chart-bar-fill" style="width: 26%;"></div></div>
                            </div>
                            <div class="chart-bar-row">
                                <div class="chart-bar-label"><span>Transport</span><span class="font-mono">₹2,370</span></div>
                                <div class="chart-bar-bg"><div class="chart-bar-fill" style="width: 13%;"></div></div>
                            </div>
                        </div>
                    </div>

                    <div class="card">
                        <div class="flex-between" style="font-size: 0.75rem;">
                            <span class="text-secondary">Monthly budget</span>
                            <span class="font-mono">74%</span>
                        </div>
                        <div class="progress-container"><div class="progress-bar" style="width: 74%;"></div></div>
                    </div>
                </div>
            </div>
        </div>
    </section>

    <!-- ============ FEATURES ============ -->
    <section class="section" id="features">
        <span class="section-eyebrow">Features</span>
        <h2 class="section-title">Everything you need, nothing you don't.</h2>
        <p class="section-lead">Each screen does one job well, with the same quiet interface throughout.</p>

        <div class="feature-grid">
            <div class="feature-card">
                <div class="feature-icon">
                    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M12 1v22M17 5H9.5a3.5 3.5 0 0 0 0 7h5a3.5 3.5 0 0 1 0 7H6"/></svg>
                </div>
                <h3>Track expenses</h3>
                <p>Record every transaction by category, amount and date. Edit or delete in one click.</p>
            </div>
            <div class="feature-card">
                <div class="feature-icon">
                    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><circle cx="12" cy="12" r="9"/><path d="M12 7v5l3 3"/></svg>
                </div>
                <h3>Set budgets</h3>
                <p>Weekly, monthly or annual limits per category, with progress bars that warn you at 80%.</p>
            </div>
            <div class="feature-card">
                <div class="feature-icon">
                    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M3 3v18h18"/><path d="M7 15l4-4 3 3 5-6"/></svg>
                </div>
                <h3>Reports</h3>
                <p>Filter by date range and category to see totals, averages and budget-vs-actual comparisons.</p>
            </div>
            <div class="feature-card">
                <div class="feature-icon">
                    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M21 15a2 2 0 0 1-2 2H7l-4 4V5a2 2 0 0 1 2-2h14a2 2 0 0 1 2 2z"/></svg>
                </div>
                <h3>Advisor guidance</h3>
                <p>Advisors review client spending and send written recommendations straight to the dashboard.</p>
            </div>
            <div class="feature-card">
                <div class="feature-icon">
                    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><rect x="3" y="11" width="18" height="11" rx="2"/><path d="M7 11V7a5 5 0 0 1 10 0v4"/></svg>
                </div>
                <h3>Role-based access</h3>
                <p>Servlet filters guard every route, so users, advisors and admins each see only their own tools.</p>
            </div>
            <div class="feature-card">
                <div class="feature-icon">
                    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M4 6h16M4 12h10M4 18h6"/><path d="M18 14l3 3-3 3"/></svg>
                </div>
                <h3>Parallel data loading</h3>
                <p>Dashboards and reports run their database queries at the same time on a shared thread pool.</p>
            </div>
        </div>
    </section>

    <!-- ============ MULTITHREADING ============ -->
    <section class="section" id="parallel">
        <div class="split">
            <div>
                <span class="section-eyebrow">Under the hood</span>
                <h2 class="section-title">Built to run in parallel.</h2>
                <p class="section-lead">
                    A dashboard needs seven independent queries. Instead of running them one after another,
                    the server forks them onto a shared worker pool and waits for all of them once.
                </p>
                <ul class="check-list">
                    <li>One shared, bounded <code>ThreadPoolExecutor</code> for the whole app, with named <code>finance-worker</code> threads.</li>
                    <li><code>CompletableFuture</code> tasks per request, so the page waits for the slowest query, not the sum of all of them.</li>
                    <li>Reports run in two phases: parallel reads, then parallel calculations that start as soon as their data is ready.</li>
                    <li>A background scheduler samples the pool every few seconds for the live monitor.</li>
                </ul>
                <a href="${ctx}/docs#multithreading" class="btn btn-secondary">See how it works &rarr;</a>
            </div>

            <!-- Illustrative timeline using the same component the dashboard renders -->
            <div class="card" aria-hidden="true">
                <div class="card-header">
                    <div class="card-title">Parallel execution</div>
                    <span class="badge">User dashboard</span>
                </div>
                <div class="parallel-summary-stats">
                    <span><strong>7</strong> tasks</span>
                    <span><strong>4</strong> threads</span>
                    <span><strong>~3&times;</strong> faster</span>
                </div>
                <div class="timeline">
                    <div class="timeline-row"><div class="timeline-label">Total expenses<span class="timeline-thread">finance-worker-1</span></div><div class="timeline-track"><div class="timeline-bar" style="left: 0%; width: 34%;"></div></div><div class="timeline-ms">12 ms</div></div>
                    <div class="timeline-row"><div class="timeline-label">Total budget<span class="timeline-thread">finance-worker-2</span></div><div class="timeline-track"><div class="timeline-bar" style="left: 1%; width: 28%;"></div></div><div class="timeline-ms">10 ms</div></div>
                    <div class="timeline-row"><div class="timeline-label">Remaining budget<span class="timeline-thread">finance-worker-3</span></div><div class="timeline-track"><div class="timeline-bar" style="left: 2%; width: 55%;"></div></div><div class="timeline-ms">19 ms</div></div>
                    <div class="timeline-row"><div class="timeline-label">Recent expenses<span class="timeline-thread">finance-worker-4</span></div><div class="timeline-track"><div class="timeline-bar" style="left: 2%; width: 30%;"></div></div><div class="timeline-ms">11 ms</div></div>
                    <div class="timeline-row"><div class="timeline-label">Category breakdown<span class="timeline-thread">finance-worker-2</span></div><div class="timeline-track"><div class="timeline-bar" style="left: 30%; width: 38%;"></div></div><div class="timeline-ms">13 ms</div></div>
                    <div class="timeline-row"><div class="timeline-label">Active budget<span class="timeline-thread">finance-worker-1</span></div><div class="timeline-track"><div class="timeline-bar" style="left: 35%; width: 62%;"></div></div><div class="timeline-ms">22 ms</div></div>
                    <div class="timeline-row"><div class="timeline-label">Advisor advice<span class="timeline-thread">finance-worker-4</span></div><div class="timeline-track"><div class="timeline-bar" style="left: 33%; width: 25%;"></div></div><div class="timeline-ms">9 ms</div></div>
                </div>
                <p class="timeline-note">Example timeline. Real timings appear at the bottom of each dashboard.</p>
            </div>
        </div>
    </section>

    <!-- ============ ROLES ============ -->
    <section class="section" id="roles">
        <span class="section-eyebrow">Roles</span>
        <h2 class="section-title">One app, three workspaces.</h2>
        <p class="section-lead">After you sign in, you're sent to the workspace for your role.</p>

        <div class="role-grid">
            <div class="role-card">
                <span class="role-pill">User</span>
                <ul>
                    <li>Add, edit and delete expenses</li>
                    <li>Create category budgets</li>
                    <li>Run filtered reports</li>
                    <li>Read advisor recommendations</li>
                    <li>Send feedback</li>
                </ul>
            </div>
            <div class="role-card">
                <span class="role-pill">Advisor</span>
                <ul>
                    <li>See all clients at a glance</li>
                    <li>Review a client's expenses</li>
                    <li>Write dated recommendations</li>
                    <li>Track advice issued</li>
                </ul>
            </div>
            <div class="role-card">
                <span class="role-pill">Admin</span>
                <ul>
                    <li>System-wide user and expense counts</li>
                    <li>Manage user accounts</li>
                    <li>Review and resolve feedback</li>
                    <li>Access advisor tools</li>
                </ul>
            </div>
        </div>
    </section>

    <!-- ============ HOW IT WORKS ============ -->
    <section class="section" id="how-it-works">
        <span class="section-eyebrow">How it works</span>
        <h2 class="section-title">From sign-up to insight in four steps.</h2>
        <p class="section-lead">No setup beyond an account. Your data stays in the app's MySQL database.</p>

        <div class="steps">
            <div class="step">
                <h3>Create an account</h3>
                <p>Register as a user, advisor or admin and sign in.</p>
            </div>
            <div class="step">
                <h3>Log expenses</h3>
                <p>Add transactions as they happen, with a category and date.</p>
            </div>
            <div class="step">
                <h3>Set budgets</h3>
                <p>Give each category a limit and watch the progress bars.</p>
            </div>
            <div class="step">
                <h3>Review &amp; improve</h3>
                <p>Use reports and advisor tips to adjust your spending.</p>
            </div>
        </div>
    </section>

    <!-- ============ CTA ============ -->
    <section class="section">
        <div class="cta-band">
            <h2 class="section-title">Start tracking today.</h2>
            <p class="section-lead">It takes less than a minute to create an account.</p>
            <div class="hero-actions">
                <a href="${ctx}/register" class="btn btn-primary">Create account</a>
                <a href="${ctx}/docs" class="btn btn-secondary">Read the docs</a>
            </div>
        </div>
    </section>

</main>

<jsp:include page="/WEB-INF/views/common/site-footer.jsp"/>

<script src="${ctx}/assets/js/main.js"></script>
</body>
</html>
