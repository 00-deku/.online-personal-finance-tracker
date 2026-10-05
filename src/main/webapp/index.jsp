<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Finance. - Personal Finance Tracker</title>

    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/responsive.css">
    <link rel="shortcut icon" href="${pageContext.request.contextPath}/assets/images/favicon.ico" type="image/x-icon">
</head>
<body>

    <!-- Minimal Header Bar -->
    <header class="app-navbar">
        <a href="${pageContext.request.contextPath}/" class="navbar-brand">
            Finance.
        </a>

        <div class="navbar-actions" style="gap: var(--space-4);">
            <a href="#features" style="font-size: 0.85rem; color: var(--text-secondary);">Features</a>
            <a href="#about" style="font-size: 0.85rem; color: var(--text-secondary);">About</a>
            <a href="${pageContext.request.contextPath}/login" style="font-size: 0.85rem; color: var(--text-secondary);">Sign in</a>
            <a href="${pageContext.request.contextPath}/register" class="btn btn-primary btn-sm">Get started</a>
        </div>
    </header>

    <!-- Centered Hero Section -->
    <main class="content-wrapper" style="max-width: 760px; padding-top: 80px; padding-bottom: 60px;">
        
        <div style="text-align: center; margin-bottom: 60px;">
            <h1 style="font-size: 2.5rem; font-weight: 500; letter-spacing: -0.03em; margin-bottom: var(--space-2); color: var(--text-primary);">
                Manage your money with clarity.
            </h1>
            <p style="font-size: 1.05rem; max-width: 520px; margin: 0 auto var(--space-4); color: var(--text-secondary); line-height: 1.6;">
                A clean, quiet personal finance tool to record daily expenses, define category budgets, receive advisor recommendations, and generate financial reports.
            </p>

            <div class="flex gap-2" style="justify-content: center;">
                <a href="${pageContext.request.contextPath}/register" class="btn btn-primary">Get started</a>
                <a href="${pageContext.request.contextPath}/login" class="btn btn-secondary">Sign in</a>
            </div>
        </div>

        <!-- Compact Feature Overview Section -->
        <div id="features" style="display: grid; grid-template-columns: repeat(auto-fit, minmax(160px, 1fr)); gap: var(--space-3); margin-bottom: 60px;">
            
            <div class="card" style="padding: var(--space-3);">
                <div class="card-title" style="margin-bottom: 4px;">Track Expenses</div>
                <p style="font-size: 0.8rem; color: var(--text-muted);">Record daily transactions by category, amount, and date.</p>
            </div>

            <div class="card" style="padding: var(--space-3);">
                <div class="card-title" style="margin-bottom: 4px;">Set Budgets</div>
                <p style="font-size: 0.8rem; color: var(--text-muted);">Define target spending limits for specified category periods.</p>
            </div>

            <div class="card" style="padding: var(--space-3);">
                <div class="card-title" style="margin-bottom: 4px;">Get Advice</div>
                <p style="font-size: 0.8rem; color: var(--text-muted);">Receive structured recommendations from financial advisors.</p>
            </div>

            <div class="card" style="padding: var(--space-3);">
                <div class="card-title" style="margin-bottom: 4px;">View Reports</div>
                <p style="font-size: 0.8rem; color: var(--text-muted);">Analyze period spending distributions and budget variance.</p>
            </div>

        </div>

        <!-- About Section -->
        <div id="about" class="card" style="text-align: center; padding: var(--space-4);">
            <div class="card-title" style="margin-bottom: 8px;">College Personal Finance Project</div>
            <p style="font-size: 0.85rem; color: var(--text-secondary); max-width: 540px; margin: 0 auto;">
                Built on Java 21, Jakarta Servlet 6.1, Tomcat 11, and MySQL. Provides a clean frontend interface ready for Servlet integration.
            </p>
        </div>

    </main>

    <!-- Footer -->
    <footer class="app-footer">
        <p>&copy; 2026 Finance. All rights reserved.</p>
    </footer>

    <script src="${pageContext.request.contextPath}/assets/js/main.js"></script>
</body>
</html>
