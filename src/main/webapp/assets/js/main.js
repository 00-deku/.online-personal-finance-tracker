/**
 * ONLINE PERSONAL FINANCE MANAGEMENT SYSTEM
 * File: main.js
 * Description: Global UI behavior, mobile navigation drawer, interactive helpers.
 */

document.addEventListener('DOMContentLoaded', function () {
    initMobileNav();
    initAlertDismissals();
});

/**
 * Initializes mobile sidebar toggle and backdrop overlay.
 */
function initMobileNav() {
    const toggleBtn = document.getElementById('mobileNavToggle');
    const sidebar = document.getElementById('appSidebar');
    const overlay = document.getElementById('sidebarOverlay');

    if (!toggleBtn || !sidebar) return;

    function openSidebar() {
        sidebar.classList.add('open');
        if (overlay) overlay.classList.add('active');
        document.body.style.overflow = 'hidden';
    }

    function closeSidebar() {
        sidebar.classList.remove('open');
        if (overlay) overlay.classList.remove('active');
        document.body.style.overflow = '';
    }

    toggleBtn.addEventListener('click', function () {
        if (sidebar.classList.contains('open')) {
            closeSidebar();
        } else {
            openSidebar();
        }
    });

    if (overlay) {
        overlay.addEventListener('click', closeSidebar);
    }
}

/**
 * Helper to dynamically show inline toast or notification message
 */
function showNotification(message, type = 'info') {
    const container = document.getElementById('notificationContainer') || createNotificationContainer();
    
    const alertDiv = document.createElement('div');
    alertDiv.className = `alert alert-${type} mb-2`;
    alertDiv.innerHTML = `<span>${message}</span>`;
    
    container.appendChild(alertDiv);

    setTimeout(() => {
        alertDiv.style.opacity = '0';
        alertDiv.style.transition = 'opacity 300ms ease';
        setTimeout(() => alertDiv.remove(), 300);
    }, 4000);
}

function createNotificationContainer() {
    const container = document.createElement('div');
    container.id = 'notificationContainer';
    container.style.position = 'fixed';
    container.style.bottom = '20px';
    container.style.right = '20px';
    container.style.zIndex = '1000';
    container.style.maxWidth = '360px';
    document.body.appendChild(container);
    return container;
}

/**
 * Initializes alert dismiss buttons if present
 */
function initAlertDismissals() {
    const alerts = document.querySelectorAll('.alert-dismissible');
    alerts.forEach(alert => {
        const closeBtn = document.createElement('button');
        closeBtn.innerHTML = '&times;';
        closeBtn.style.background = 'none';
        closeBtn.style.border = 'none';
        closeBtn.style.color = 'inherit';
        closeBtn.style.cursor = 'pointer';
        closeBtn.style.float = 'right';
        closeBtn.style.fontSize = '1.2rem';
        closeBtn.addEventListener('click', () => alert.remove());
        alert.insertBefore(closeBtn, alert.firstChild);
    });
}
