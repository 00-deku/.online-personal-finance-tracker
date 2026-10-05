<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Sign In - Finance.</title>

    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/auth.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/responsive.css">
    <link rel="shortcut icon" href="${pageContext.request.contextPath}/assets/images/favicon.ico" type="image/x-icon">
</head>
<body>

<div class="auth-wrapper">
    <div class="auth-card">
        
        <div class="auth-header">
            <a href="${pageContext.request.contextPath}/" class="auth-brand">
                Finance.
            </a>
            <h1 class="auth-title">Welcome back</h1>
            <p class="auth-subtitle">Sign in to your account.</p>
        </div>

        <%-- Display errorMessage from Servlet if authentication fails --%>
        <% if (request.getAttribute("errorMessage") != null) { %>
            <div class="alert alert-error mb-3">
                <%= request.getAttribute("errorMessage") %>
            </div>
        <% } %>

        <form id="loginForm" method="post" action="${pageContext.request.contextPath}/login" class="auth-form" novalidate>
            
            <div class="form-group">
                <label for="email" class="form-label">Email</label>
                <input type="email" id="email" name="email" class="form-control" placeholder="name@domain.com" required autocomplete="email">
                <span class="form-error">Please enter a valid email address.</span>
            </div>

            <div class="form-group">
                <label for="password" class="form-label">Password</label>
                <input type="password" id="password" name="password" class="form-control" placeholder="••••••••" required autocomplete="current-password">
                <span class="form-error">Password is required.</span>
            </div>

            <div class="auth-options">
                <label class="checkbox-group">
                    <input type="checkbox" name="rememberMe" value="true">
                    <span>Remember me</span>
                </label>

                <a href="#" class="auth-forgot-link" onclick="alert('Password recovery is handled by administrator.'); return false;">Forgot password?</a>
            </div>

            <button type="submit" class="btn btn-primary btn-block mt-2">Sign in</button>
        </form>

        <div class="auth-footer">
            Don't have an account? <a href="${pageContext.request.contextPath}/register">Create one</a>
        </div>

    </div>
</div>

<script src="${pageContext.request.contextPath}/assets/js/main.js"></script>
<script src="${pageContext.request.contextPath}/assets/js/validation.js"></script>
</body>
</html>
