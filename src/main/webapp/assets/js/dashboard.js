/**
 * ONLINE PERSONAL FINANCE MANAGEMENT SYSTEM
 * File: dashboard.js
 * Description: Dashboard UI behaviors, client-side budget progress calculations, date helpers, confirmation dialogues.
 */

document.addEventListener('DOMContentLoaded', function () {
    initDefaultDates();
    initBudgetProgress();
    initDeleteConfirmations();
});

/**
 * Automatically sets today's date on date inputs if empty
 */
function initDefaultDates() {
    const dateInputs = document.querySelectorAll('input[type="date"]');
    const today = new Date().toISOString().split('T')[0];
    dateInputs.forEach(input => {
        if (!input.value) {
            input.value = today;
        }
    });
}

/**
 * Calculates remaining budget dynamically on budget cards or table rows if demo elements exist
 */
function initBudgetProgress() {
    const budgetRows = document.querySelectorAll('[data-budget-amount]');
    budgetRows.forEach(row => {
        const amount = parseFloat(row.getAttribute('data-budget-amount')) || 0;
        const spent = parseFloat(row.getAttribute('data-budget-spent')) || 0;
        const progressBar = row.querySelector('.progress-bar');
        const remainingSpan = row.querySelector('.budget-remaining');

        if (amount > 0) {
            const percentage = Math.min((spent / amount) * 100, 100);
            if (progressBar) {
                progressBar.style.width = percentage + '%';
                if (percentage >= 100) {
                    progressBar.className = 'progress-bar danger';
                } else if (percentage >= 80) {
                    progressBar.className = 'progress-bar warning';
                }
            }
            if (remainingSpan) {
                const remaining = amount - spent;
                remainingSpan.textContent = remaining >= 0 ? `₹${remaining.toFixed(2)} remaining` : `Exceeded by ₹${Math.abs(remaining).toFixed(2)}`;
            }
        }
    });
}

/**
 * Prompts user confirmation prior to deleting records (Expense, Budget, Advice, User)
 */
function initDeleteConfirmations() {
    const deleteBtns = document.querySelectorAll('.btn-delete-confirm');
    deleteBtns.forEach(btn => {
        btn.addEventListener('click', function (e) {
            const item = btn.getAttribute('data-item-name') || 'this item';
            if (!confirm(`Are you sure you want to delete ${item}? This action cannot be undone.`)) {
                e.preventDefault();
            }
        });
    });
}
