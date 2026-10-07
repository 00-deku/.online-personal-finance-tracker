<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Docs - Finance.</title>

    <link rel="stylesheet" href="${ctx}/assets/css/style.css">
    <link rel="stylesheet" href="${ctx}/assets/css/dashboard.css">
    <link rel="stylesheet" href="${ctx}/assets/css/landing.css">
    <link rel="stylesheet" href="${ctx}/assets/css/responsive.css">
    <link rel="shortcut icon" href="${ctx}/assets/images/favicon.ico" type="image/x-icon">
</head>
<body>

<jsp:include page="common/site-nav.jsp">
    <jsp:param name="active" value="docs"/>
</jsp:include>

<div class="docs-layout">

    <!-- Table of contents -->
    <nav class="docs-toc" id="docsToc" aria-label="Documentation">
        <div class="sidebar-heading">Documentation</div>
        <a href="#overview">Overview</a>
        <a href="#getting-started">Getting started</a>
        <a href="#architecture">Architecture</a>
        <a href="#request-flow">Request lifecycle</a>
        <a href="#roles">Roles &amp; security</a>
        <a href="#features">Using the app</a>
        <a href="#multithreading">Multithreading</a>
        <a href="#live-monitor">Live thread monitor</a>
        <a href="#structure">Project structure</a>
    </nav>

    <main class="docs-content">

        <!-- ============ OVERVIEW ============ -->
        <section class="docs-section" id="overview">
            <span class="section-eyebrow">Docs</span>
            <h1 class="section-title" style="font-size: 2rem;">How Finance. works</h1>
            <p>
                Finance. is a personal finance tracker built with <strong>Jakarta Servlets</strong>, <strong>JSP</strong>,
                <strong>JDBC</strong> and <strong>MySQL</strong>, packaged as a WAR and deployed on <strong>Apache Tomcat 11</strong>.
                Users record expenses and budgets, advisors send recommendations, and admins manage accounts and feedback.
            </p>
            <p>
                This page explains how a request moves through the app, how access is controlled, and how the server
                uses <a href="#multithreading" style="text-decoration: underline;">multithreading</a> to load dashboards and reports faster.
                The <a href="#live-monitor" style="text-decoration: underline;">live monitor</a> at the bottom shows the thread pool working in real time.
            </p>
        </section>

        <!-- ============ GETTING STARTED ============ -->
        <section class="docs-section" id="getting-started">
            <h2>Getting started</h2>
            <ol>
                <li>Create the MySQL database and the five tables: <code>user</code>, <code>expenses</code>, <code>budgets</code>, <code>advice</code>, <code>feedback</code>.</li>
                <li>Add <code>src/main/resources/db.properties</code> (it is git-ignored):</li>
            </ol>
<div class="code-block">db.url=jdbc:mysql://localhost:3306/suspicious4
db.user=root
db.password=your-password</div>
            <ol start="3">
                <li>Build the WAR with Maven and deploy it to Tomcat 11:</li>
            </ol>
<div class="code-block">mvn clean package
cp target/finance.war $CATALINA_HOME/webapps/
<span class="c"># open http://localhost:8080/finance/</span></div>
            <p>Register an account, sign in, and you are redirected to the dashboard for your role.</p>
        </section>

        <!-- ============ ARCHITECTURE ============ -->
        <section class="docs-section" id="architecture">
            <h2>Architecture</h2>
            <p>The code is split into layers. Each layer only talks to the one below it, which keeps SQL out of the servlets and HTML out of the services.</p>

            <div class="layer-stack">
                <div class="layer"><strong>JSP views</strong><span>Render HTML from request attributes with JSTL (<code>WEB-INF/views</code>)</span></div>
                <div class="layer-arrow">&darr;</div>
                <div class="layer"><strong>Filters</strong><span><code>AuthFilter</code> and <code>RoleFilter</code> check the session and role on every protected URL</span></div>
                <div class="layer-arrow">&darr;</div>
                <div class="layer"><strong>Servlets</strong><span>Read parameters, call services, set attributes, forward to a JSP</span></div>
                <div class="layer-arrow">&darr;</div>
                <div class="layer"><strong>Services</strong><span>Validation and business rules, plus parallel work via <code>ParallelBatch</code></span></div>
                <div class="layer-arrow">&darr;</div>
                <div class="layer"><strong>DAOs</strong><span>One class per table, plain JDBC with <code>PreparedStatement</code></span></div>
                <div class="layer-arrow">&darr;</div>
                <div class="layer"><strong>MySQL</strong><span>Connections come from <code>DBConnection</code>, configured by <code>db.properties</code></span></div>
            </div>
        </section>

        <!-- ============ REQUEST FLOW ============ -->
        <section class="docs-section" id="request-flow">
            <h2>Request lifecycle</h2>
            <p>Here is what happens when a signed-in user opens <code>/user/dashboard</code>:</p>
            <ol>
                <li><strong>Tomcat</strong> takes a thread from its HTTP connector pool to handle the request.</li>
                <li><strong>AuthFilter</strong> checks that the session has a <code>user</code>. If not, it redirects to <code>/login</code>.</li>
                <li><strong>RoleFilter</strong> checks that the role may open the path (<code>/admin/*</code> needs ADMIN, <code>/advisor/*</code> needs ADVISOR or ADMIN). Otherwise it returns 403.</li>
                <li><strong>UserDashboardServlet</strong> forks seven queries onto the worker pool and waits for them together (see <a href="#multithreading" style="text-decoration: underline;">Multithreading</a>).</li>
                <li>The results are stored as request attributes and the request is forwarded to <code>dashboard.jsp</code>.</li>
                <li>The JSP renders HTML using shared layout pieces: <code>header</code>, <code>navbar</code>, <code>sidebar</code> and <code>footer</code>.</li>
            </ol>
            <p>Forms such as Add Expense use the <strong>Post/Redirect/Get</strong> pattern: the servlet saves the data, then redirects, so refreshing the page never submits the form twice.</p>
        </section>

        <!-- ============ ROLES ============ -->
        <section class="docs-section" id="roles">
            <h2>Roles &amp; security</h2>
            <div class="table-container mb-3">
                <table class="table">
                    <thead>
                        <tr><th>Role</th><th>Home</th><th>Can access</th></tr>
                    </thead>
                    <tbody>
                        <tr><td><span class="role-pill">USER</span></td><td><code>/user/dashboard</code></td><td>Expenses, budgets, reports, profile, feedback</td></tr>
                        <tr><td><span class="role-pill">ADVISOR</span></td><td><code>/advisor/dashboard</code></td><td>Client list, client expenses, sending advice</td></tr>
                        <tr><td><span class="role-pill">ADMIN</span></td><td><code>/admin/dashboard</code></td><td>Users, feedback, system stats, plus advisor pages</td></tr>
                    </tbody>
                </table>
            </div>
            <ul>
                <li>The session is created on login and removed by <code>LogoutServlet</code>.</li>
                <li>All SQL uses <code>PreparedStatement</code> parameters, which prevents SQL injection.</li>
                <li>Expense updates and deletes, and budget deletes, include <code>user_id</code> in the <code>WHERE</code> clause, so users can only change their own records.</li>
                <li>Error pages for 403, 404 and 500 are mapped in <code>web.xml</code>.</li>
            </ul>
        </section>

        <!-- ============ FEATURES ============ -->
        <section class="docs-section" id="features">
            <h2>Using the app</h2>
            <h3>Dashboard</h3>
            <p>Total expenses, total budget and remaining budget at the top. Below them: recent expenses, spending by category, budget status and the latest advisor recommendations.</p>
            <h3>Expenses</h3>
            <p>A list of all your transactions. <strong>+ Add Expense</strong> opens a form for category, amount and date (today is filled in automatically). Each row has Edit and Delete buttons, and Delete asks for confirmation.</p>
            <h3>Budgets</h3>
            <p>Set a limit per category for a week, month or year. A budget's progress bar turns amber at 80% of the limit and red once it is exceeded.</p>
            <h3>Reports</h3>
            <p>Filter by date range and category to see total and average spending, a category breakdown, and a budget-vs-actual table. The report is built in parallel, and the <strong>Parallel execution</strong> panel at the bottom shows exactly how.</p>
            <h3>Profile &amp; feedback</h3>
            <p>View your account details and send feedback to the admins, who can mark it as resolved.</p>
        </section>

        <!-- ============ MULTITHREADING ============ -->
        <section class="docs-section" id="multithreading">
            <h2>Multithreading</h2>
            <p>
                Most of the time a page spends loading goes to <strong>waiting on the database</strong>. The user dashboard needs seven
                independent results (totals, recent expenses, category breakdown, budget status, advice, and so on). Run one after
                another, the page waits for the <em>sum</em> of all seven. Run in parallel, it waits only for the <em>slowest</em> one.
            </p>

            <div class="compare-grid" aria-hidden="true">
                <div class="card">
                    <div class="card-title" style="font-size: 0.88rem;">Sequential</div>
                    <div class="timeline">
                        <div class="timeline-row"><div class="timeline-label">Query 1</div><div class="timeline-track"><div class="timeline-bar" style="left: 0%; width: 24%;"></div></div></div>
                        <div class="timeline-row"><div class="timeline-label">Query 2</div><div class="timeline-track"><div class="timeline-bar" style="left: 24%; width: 20%;"></div></div></div>
                        <div class="timeline-row"><div class="timeline-label">Query 3</div><div class="timeline-track"><div class="timeline-bar" style="left: 44%; width: 30%;"></div></div></div>
                        <div class="timeline-row"><div class="timeline-label">Query 4</div><div class="timeline-track"><div class="timeline-bar" style="left: 74%; width: 26%;"></div></div></div>
                    </div>
                    <div class="compare-total">Total = <strong>sum</strong> of all queries</div>
                </div>
                <div class="card">
                    <div class="card-title" style="font-size: 0.88rem;">Parallel (this app)</div>
                    <div class="timeline">
                        <div class="timeline-row"><div class="timeline-label">Query 1</div><div class="timeline-track"><div class="timeline-bar" style="left: 0%; width: 24%;"></div></div></div>
                        <div class="timeline-row"><div class="timeline-label">Query 2</div><div class="timeline-track"><div class="timeline-bar" style="left: 1%; width: 20%;"></div></div></div>
                        <div class="timeline-row"><div class="timeline-label">Query 3</div><div class="timeline-track"><div class="timeline-bar" style="left: 1%; width: 30%;"></div></div></div>
                        <div class="timeline-row"><div class="timeline-label">Query 4</div><div class="timeline-track"><div class="timeline-bar" style="left: 2%; width: 26%;"></div></div></div>
                    </div>
                    <div class="compare-total">Total = <strong>slowest</strong> query</div>
                </div>
            </div>

            <h3>1. One shared worker pool &mdash; <code>TaskExecutor</code></h3>
            <p>
                The whole app shares one <code>ThreadPoolExecutor</code>. Creating a pool for every request would start and stop
                threads constantly, so the pool's threads are reused instead. It is bounded, which also limits how many database
                connections can be open at once.
            </p>
            <div class="table-container mb-3">
                <table class="table">
                    <tbody>
                        <tr><td>Core threads</td><td class="font-mono">${coreThreads}</td><td class="text-muted">max(4, CPU cores)</td></tr>
                        <tr><td>Max threads</td><td class="font-mono">${maxThreads}</td><td class="text-muted">2 &times; core, used only when the queue is full</td></tr>
                        <tr><td>Queue capacity</td><td class="font-mono">${queueCapacity}</td><td class="text-muted">bounded <code>ArrayBlockingQueue</code></td></tr>
                        <tr><td>When saturated</td><td class="font-mono">CallerRuns</td><td class="text-muted">the request thread runs the task itself (back-pressure, nothing is dropped)</td></tr>
                        <tr><td>Thread names</td><td class="font-mono">finance-worker-N</td><td class="text-muted">daemon threads, easy to spot in logs and below</td></tr>
                    </tbody>
                </table>
            </div>

            <h3>2. Forking a request's work &mdash; <code>ParallelBatch</code></h3>
            <p>
                A servlet creates a <code>ParallelBatch</code>, forks each independent query with <code>fork(label, task)</code>, and calls
                <code>awaitAll()</code> once. Every task returns a <code>CompletableFuture</code> right away, so all of them are running before the request thread starts waiting.
            </p>
<div class="code-block"><span class="c">// UserDashboardServlet.doGet (simplified)</span>
ParallelBatch batch = new ParallelBatch("User dashboard");

CompletableFuture&lt;BigDecimal&gt; total   = batch.fork("Total expenses",  () -&gt; expenseService.getTotalExpenses(userId));
CompletableFuture&lt;BigDecimal&gt; budget  = batch.fork("Total budget",    () -&gt; budgetService.getTotalBudget(userId));
CompletableFuture&lt;List&lt;Expense&gt;&gt; recent = batch.fork("Recent expenses", () -&gt; expenseService.getRecentExpenses(userId, 5));
<span class="c">// ... four more</span>

batch.awaitAll();                          <span class="c">// wait once for all seven</span>
BigDecimal t = ParallelBatch.result(total);  <span class="c">// task errors come back as DatabaseException</span></div>
            <p>Each task records the thread that ran it, when it started and how long it took. That is what the <strong>Parallel execution</strong> panel at the bottom of each dashboard shows.</p>

            <p>Pages that use it:</p>
            <ul>
                <li><strong>User dashboard</strong> &mdash; 7 parallel queries</li>
                <li><strong>Admin dashboard</strong> &mdash; 5 parallel queries (counts, users, feedback)</li>
                <li><strong>Advisor dashboard</strong> &mdash; 4 parallel queries</li>
                <li><strong>Reports</strong> &mdash; 6 tasks in two phases (below)</li>
            </ul>

            <h3>3. Two-phase reports with dependencies</h3>
            <p>
                <code>ReportService</code> has work that depends on other work. <strong>Phase 1</strong> loads expenses, budgets and the
                category list in parallel. <strong>Phase 2</strong> computes the totals, the category breakdown and the budget comparison.
                Phase 2 tasks are chained with <code>thenCompose</code> / <code>thenCombine</code>, so each one starts as soon as
                <em>its own</em> inputs are ready, without waiting for the rest of phase 1.
            </p>
<div class="code-block">expensesF ──┬─▶ Compute total &amp; average
            ├─▶ Compute category breakdown
budgetsF  ──┴─▶ Compute budget vs actual   <span class="c">(thenCombine: needs both)</span>
categoriesF ──▶ filter dropdown</div>

            <h3>4. Background scheduler</h3>
            <p>
                <code>AppLifecycleListener</code> (a <code>@WebListener</code>) starts the pool when Tomcat deploys the app. It also starts a
                <code>ScheduledExecutorService</code> on a thread called <code>finance-scheduler</code>, which records the pool's size,
                active threads and queue depth every <strong>${sampleInterval} seconds</strong>. When the app is undeployed, both are shut
                down so no threads leak between redeployments.
            </p>

            <h3>5. Why it is thread-safe</h3>
            <ul>
                <li><strong>No shared connections.</strong> Each DAO call opens its own JDBC <code>Connection</code> in try-with-resources, so tasks never share one.</li>
                <li><strong>Stateless services.</strong> Services and DAOs hold no per-request state, so one instance can be used by many threads at once.</li>
                <li><strong>Read-only inputs.</strong> Phase 2 report tasks only read the lists from phase 1 and build new results.</li>
                <li><strong>Concurrent collections.</strong> Task records use <code>CopyOnWriteArrayList</code>, the monitor uses <code>AtomicLong</code> counters and <code>ConcurrentLinkedDeque</code> buffers, and the pool singleton uses a <code>volatile</code> field with double-checked locking.</li>
                <li><strong>No pool deadlock.</strong> Worker tasks never block waiting for other tasks on the same pool. Only the request thread waits.</li>
            </ul>
        </section>

        <!-- ============ LIVE MONITOR ============ -->
        <section class="docs-section" id="live-monitor">
            <h2>Live thread monitor</h2>
            <p>
                <span class="live-dot" id="liveDot"></span><span id="liveStatus" class="text-muted">Connecting&hellip;</span>
                &nbsp;Data from <code>/api/concurrency</code>, refreshed every 3 seconds. Open a dashboard or report in another tab and watch the numbers change.
            </p>

            <div class="monitor-stats">
                <div class="stat-card">
                    <div class="stat-label">Pool threads</div>
                    <div class="stat-value" id="mPool">${poolSize}</div>
                    <div class="stat-subtext"><span id="mActive">${activeThreads}</span> active now</div>
                </div>
                <div class="stat-card">
                    <div class="stat-label">Tasks done</div>
                    <div class="stat-value" id="mCompleted">${tasksCompleted}</div>
                    <div class="stat-subtext"><span id="mFailed">0</span> failed</div>
                </div>
                <div class="stat-card">
                    <div class="stat-label">Batches</div>
                    <div class="stat-value" id="mBatches">${batchesRun}</div>
                    <div class="stat-subtext">page loads in parallel</div>
                </div>
                <div class="stat-card">
                    <div class="stat-label">Time saved</div>
                    <div class="stat-value" id="mSaved">0 ms</div>
                    <div class="stat-subtext">vs one-by-one</div>
                </div>
            </div>

            <div class="card mb-3">
                <div class="card-header">
                    <div class="card-title">Pool activity</div>
                    <span class="card-subtitle">pool size &amp; active threads, last ~2.5 min</span>
                </div>
                <svg class="sparkline" id="sparkline" viewBox="0 0 300 64" preserveAspectRatio="none" role="img" aria-label="Pool activity over time"></svg>
                <div class="thread-chips" id="threadChips"></div>
            </div>

            <div class="card mb-3">
                <div class="card-header">
                    <div class="card-title">Recent batches</div>
                </div>
                <div class="table-container">
                    <table class="table">
                        <thead>
                            <tr><th>Page</th><th class="text-right">Tasks</th><th class="text-right">Threads</th><th class="text-right">Wall</th><th class="text-right">One-by-one</th></tr>
                        </thead>
                        <tbody id="batchRows">
                            <c:forEach items="${recentBatches}" var="b">
                                <tr>
                                    <td>${b.name}</td>
                                    <td class="text-right font-mono">${b.taskCount}</td>
                                    <td class="text-right font-mono">${b.threadCount}</td>
                                    <td class="text-right font-mono">${b.wallMs} ms</td>
                                    <td class="text-right font-mono">${b.sequentialMs} ms</td>
                                </tr>
                            </c:forEach>
                            <c:if test="${empty recentBatches}">
                                <tr><td colspan="5" class="text-muted">No batches yet. Open a dashboard or report to generate some.</td></tr>
                            </c:if>
                        </tbody>
                    </table>
                </div>
            </div>

            <div class="card">
                <div class="card-header">
                    <div class="card-title">Recent tasks</div>
                </div>
                <div class="table-container">
                    <table class="table">
                        <thead>
                            <tr><th>Task</th><th>Thread</th><th class="text-right">Time</th><th class="text-right">Status</th></tr>
                        </thead>
                        <tbody id="taskRows">
                            <c:forEach items="${recentTasks}" var="t" end="11">
                                <tr>
                                    <td>${t.label} <span class="text-muted">&middot; ${t.batchName}</span></td>
                                    <td class="font-mono text-muted">${t.threadName}</td>
                                    <td class="text-right font-mono">${t.durationMs} ms</td>
                                    <td class="text-right"><span class="badge">${t.status}</span></td>
                                </tr>
                            </c:forEach>
                            <c:if test="${empty recentTasks}">
                                <tr><td colspan="4" class="text-muted">No tasks yet.</td></tr>
                            </c:if>
                        </tbody>
                    </table>
                </div>
            </div>
        </section>

        <!-- ============ STRUCTURE ============ -->
        <section class="docs-section" id="structure">
            <h2>Project structure</h2>
<div class="code-block">src/main/java/com/finance/
├── concurrent/      <span class="c">TaskExecutor, ParallelBatch, ConcurrencyMonitor, AppLifecycleListener</span>
├── dao/             <span class="c">UserDAO, ExpenseDAO, BudgetDAO, AdviceDAO, FeedbackDAO</span>
├── exception/       <span class="c">DatabaseException, ValidationException, ...</span>
├── filter/          <span class="c">AuthFilter, RoleFilter</span>
├── model/           <span class="c">User, Expense, Budget, Advice, Feedback, ...</span>
├── service/         <span class="c">business rules (ReportService runs in parallel)</span>
├── servlet/         <span class="c">auth/, user/, advisor/, admin/, DocsServlet, ConcurrencyStatsServlet</span>
└── util/            <span class="c">IDGenerator, ValidationUtil</span>

src/main/webapp/
├── index.jsp        <span class="c">landing page</span>
├── assets/css/      <span class="c">style, dashboard, landing, auth, responsive</span>
├── assets/js/       <span class="c">main, validation, dashboard, docs</span>
└── WEB-INF/views/   <span class="c">auth/, user/, advisor/, admin/, common/, error/, docs.jsp</span></div>
        </section>

    </main>
</div>

<jsp:include page="common/site-footer.jsp"/>

<script>window.FINANCE_CTX = '${ctx}';</script>
<script src="${ctx}/assets/js/main.js"></script>
<script src="${ctx}/assets/js/docs.js"></script>
</body>
</html>
