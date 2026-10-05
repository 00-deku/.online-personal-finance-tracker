<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<jsp:include page="../common/header.jsp">
    <jsp:param name="pageTitle" value="Profile - Finance."/>
</jsp:include>

<jsp:include page="../common/navbar.jsp">
    <jsp:param name="role" value="USER"/>
</jsp:include>

<div class="app-container">
    <jsp:include page="../common/sidebar.jsp">
        <jsp:param name="activePage" value="profile"/>
    </jsp:include>

    <main class="main-content">
        <div class="content-wrapper" style="max-width: 640px;">
            
            <div class="page-header">
                <div>
                    <h1 class="page-title">Profile</h1>
                    <p class="page-subtitle">Your account information and feedback.</p>
                </div>
            </div>

            <!-- Account Details Card -->
            <div class="card mb-4">
                <div class="card-header">
                    <div class="card-title">Account Details</div>
                </div>

                <c:choose>
                    <c:when test="${not empty sessionScope.user}">
                        <div class="form-group">
                            <label class="form-label">User ID</label>
                            <input type="text" class="form-control" value="${sessionScope.user.id}" readonly>
                        </div>

                        <div class="form-group">
                            <label class="form-label">Name</label>
                            <input type="text" class="form-control" value="${sessionScope.user.name}" readonly>
                        </div>

                        <div class="form-group">
                            <label class="form-label">Email</label>
                            <input type="text" class="form-control" value="${sessionScope.user.email}" readonly>
                        </div>

                        <div class="form-group">
                            <label class="form-label">Role</label>
                            <input type="text" class="form-control" value="${sessionScope.user.role}" readonly>
                        </div>
                    </c:when>
                    <c:otherwise>
                        <div class="empty-state">
                            <div class="empty-state-title">User profile data unavailable.</div>
                            <div class="empty-state-text">Session data will be loaded upon login.</div>
                        </div>
                    </c:otherwise>
                </c:choose>
            </div>

            <!-- Password Change Placeholder Form -->
            <div class="card mb-4">
                <div class="card-header">
                    <div class="card-title">Change Password</div>
                </div>

                <form method="post" action="${pageContext.request.contextPath}/user/update-password">
                    <div class="form-group">
                        <label for="oldPassword" class="form-label">Current Password</label>
                        <input type="password" id="oldPassword" name="oldPassword" class="form-control" placeholder="••••••••" maxlength="16">
                    </div>

                    <div class="form-group">
                        <label for="newPassword" class="form-label">New Password</label>
                        <input type="password" id="newPassword" name="newPassword" class="form-control" placeholder="••••••••" maxlength="16">
                    </div>

                    <button type="submit" class="btn btn-secondary">Update Password</button>
                </form>
            </div>

            <!-- Submit Feedback Form -->
            <div class="card">
                <div class="card-header">
                    <div class="card-title">Submit Feedback</div>
                </div>

                <form id="feedbackForm" method="post" action="${pageContext.request.contextPath}/feedback/submit" novalidate>
                    <input type="hidden" name="user_id" value="${sessionScope.user.id}">

                    <div class="form-group">
                        <label for="message" class="form-label">Message</label>
                        <textarea id="message" name="message" class="form-control" placeholder="Write feedback or report an issue..." maxlength="100" required></textarea>
                        <span class="form-hint">Maximum 100 characters</span>
                        <span class="form-error">Feedback message cannot be empty (max 100 characters).</span>
                    </div>

                    <button type="submit" class="btn btn-primary">Submit Feedback</button>
                </form>
            </div>

        </div>
    </main>
</div>

<jsp:include page="../common/footer.jsp"/>
