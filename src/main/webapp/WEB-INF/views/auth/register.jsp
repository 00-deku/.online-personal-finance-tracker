<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Create Account - Finance.</title>

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
            <h1 class="auth-title">Create an account</h1>
            <p class="auth-subtitle">Enter your details to register.</p>
        </div>

        <% if (request.getAttribute("errorMessage") != null) { %>
            <div class="alert alert-error mb-3">
                <%= request.getAttribute("errorMessage") %>
            </div>
        <% } %>

        <form id="registerForm" method="post" action="${pageContext.request.contextPath}/register" class="auth-form" novalidate>
            
            <div class="form-group">
                <label for="name" class="form-label">Full Name</label>
                <input type="text" id="name" name="name" class="form-control" placeholder="John Doe" maxlength="30" required autocomplete="name">
                <span class="form-error">Full name is required (max 30 characters).</span>
            </div>

            <div class="form-group">
                <label for="email" class="form-label">Email</label>
                <input type="email" id="email" name="email" class="form-control" placeholder="name@domain.com" maxlength="50" required autocomplete="email">
                <span class="form-error">Please enter a valid email address.</span>
            </div>

            <div class="form-group">
                <label for="password" class="form-label">Password</label>
                <input type="password" id="password" name="password" class="form-control" placeholder="••••••••" maxlength="16" required autocomplete="new-password">
                <span class="form-error">Password must be between 6 and 16 characters.</span>
            </div>

            <div class="form-group">
                <label for="role" class="form-label">Role</label>
                <select id="role" name="role" class="form-control" required>
                    <option value="" disabled selected>Select Role</option>
                    <option value="USER">USER</option>
                    <option value="ADVISOR">ADVISOR</option>
                    <option value="ADMIN">ADMIN</option>
                </select>
                <span class="form-error">Please select a role.</span>
            </div>

            <button type="submit" class="btn btn-primary btn-block mt-3">Create account</button>
        </form>

        <div class="auth-footer">
            Already have an account? <a href="${pageContext.request.contextPath}/login">Sign in</a>
        </div>

    </div>
</div>

<script src="${pageContext.request.contextPath}/assets/js/main.js"></script>
<script src="${pageContext.request.contextPath}/assets/js/validation.js"></script>
</body>
</html>
