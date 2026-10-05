<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8" isErrorPage="true" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>404 - Not Found - Finance.</title>

    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/auth.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/responsive.css">
    <link rel="shortcut icon" href="${pageContext.request.contextPath}/assets/images/favicon.ico" type="image/x-icon">
</head>
<body>

<div class="auth-wrapper">
    <div class="auth-card" style="text-align: center;">
        
        <a href="${pageContext.request.contextPath}/" class="auth-brand">
            Finance.
        </a>

        <h1 style="font-size: 3.5rem; color: var(--text-primary); font-family: var(--font-mono); margin: var(--space-2) 0;">404</h1>
        <h2 class="auth-title">Page Not Found</h2>
        <p class="auth-subtitle mb-4">
            The requested page could not be found.
        </p>

        <div class="flex gap-2" style="justify-content: center;">
            <a href="${pageContext.request.contextPath}/" class="btn btn-primary">Home</a>
            <a href="javascript:history.back()" class="btn btn-secondary">Go back</a>
        </div>

    </div>
</div>

</body>
</html>
