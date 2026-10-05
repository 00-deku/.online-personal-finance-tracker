/**
 * ONLINE PERSONAL FINANCE MANAGEMENT SYSTEM
 * File: validation.js
 * Description: Client-side validation for authentication, expenses, budgets, advice, and feedback forms.
 */

document.addEventListener('DOMContentLoaded', function () {
    bindLoginFormValidation();
    bindRegisterFormValidation();
    bindExpenseFormValidation();
    bindBudgetFormValidation();
    bindFeedbackFormValidation();
    bindAdviceFormValidation();
});

/**
 * Validates Login Form
 */
function bindLoginFormValidation() {
    const form = document.getElementById('loginForm');
    if (!form) return;

    form.addEventListener('submit', function (e) {
        let isValid = true;
        clearFormErrors(form);

        const email = form.querySelector('[name="email"]');
        const password = form.querySelector('[name="password"]');

        if (!email || !validateEmail(email.value)) {
            showError(email, 'Please enter a valid email address.');
            isValid = false;
        }

        if (!password || password.value.trim() === '') {
            showError(password, 'Password is required.');
            isValid = false;
        }

        if (!isValid) {
            e.preventDefault();
        }
    });
}

/**
 * Validates Registration Form
 * Database constraints:
 * - name: VARCHAR(30)
 * - email: VARCHAR(50)
 * - password: VARCHAR(16)
 * - role: VARCHAR(10)
 */
function bindRegisterFormValidation() {
    const form = document.getElementById('registerForm');
    if (!form) return;

    form.addEventListener('submit', function (e) {
        let isValid = true;
        clearFormErrors(form);

        const name = form.querySelector('[name="name"]');
        const email = form.querySelector('[name="email"]');
        const password = form.querySelector('[name="password"]');
        const role = form.querySelector('[name="role"]');

        if (!name || name.value.trim() === '') {
            showError(name, 'Full name is required.');
            isValid = false;
        } else if (name.value.trim().length > 30) {
            showError(name, 'Name cannot exceed 30 characters.');
            isValid = false;
        }

        if (!email || !validateEmail(email.value)) {
            showError(email, 'Please enter a valid email address.');
            isValid = false;
        } else if (email.value.length > 50) {
            showError(email, 'Email cannot exceed 50 characters.');
            isValid = false;
        }

        if (!password || password.value.length < 6) {
            showError(password, 'Password must be at least 6 characters.');
            isValid = false;
        } else if (password.value.length > 16) {
            showError(password, 'Password cannot exceed 16 characters.');
            isValid = false;
        }

        if (!role || role.value === '') {
            showError(role, 'Please select a role.');
            isValid = false;
        }

        if (!isValid) {
            e.preventDefault();
        }
    });
}

/**
 * Validates Expense Form
 * Database constraints:
 * - category: VARCHAR(15)
 * - amount: DECIMAL(10,2)
 * - date: DATE
 */
function bindExpenseFormValidation() {
    const form = document.getElementById('expenseForm');
    if (!form) return;

    form.addEventListener('submit', function (e) {
        let isValid = true;
        clearFormErrors(form);

        const category = form.querySelector('[name="category"]');
        const amount = form.querySelector('[name="amount"]');
        const date = form.querySelector('[name="date"]');

        if (!category || category.value.trim() === '') {
            showError(category, 'Expense category is required.');
            isValid = false;
        } else if (category.value.length > 15) {
            showError(category, 'Category cannot exceed 15 characters.');
            isValid = false;
        }

        if (!amount || isNaN(amount.value) || parseFloat(amount.value) <= 0) {
            showError(amount, 'Please enter a valid positive amount.');
            isValid = false;
        }

        if (!date || date.value === '') {
            showError(date, 'Expense date is required.');
            isValid = false;
        }

        if (!isValid) {
            e.preventDefault();
        }
    });
}

/**
 * Validates Budget Form
 * Database constraints:
 * - category: VARCHAR(20)
 * - amount: DECIMAL(10,2)
 * - period: VARCHAR(20)
 */
function bindBudgetFormValidation() {
    const form = document.getElementById('budgetForm');
    if (!form) return;

    form.addEventListener('submit', function (e) {
        let isValid = true;
        clearFormErrors(form);

        const category = form.querySelector('[name="category"]');
        const amount = form.querySelector('[name="amount"]');
        const period = form.querySelector('[name="period"]');

        if (!category || category.value.trim() === '') {
            showError(category, 'Budget category is required.');
            isValid = false;
        } else if (category.value.length > 20) {
            showError(category, 'Category cannot exceed 20 characters.');
            isValid = false;
        }

        if (!amount || isNaN(amount.value) || parseFloat(amount.value) <= 0) {
            showError(amount, 'Please enter a valid target budget amount.');
            isValid = false;
        }

        if (!period || period.value.trim() === '') {
            showError(period, 'Budget period is required.');
            isValid = false;
        } else if (period.value.length > 20) {
            showError(period, 'Period cannot exceed 20 characters.');
            isValid = false;
        }

        if (!isValid) {
            e.preventDefault();
        }
    });
}

/**
 * Validates Feedback Form
 * Database constraints:
 * - message: VARCHAR(100)
 */
function bindFeedbackFormValidation() {
    const form = document.getElementById('feedbackForm');
    if (!form) return;

    form.addEventListener('submit', function (e) {
        let isValid = true;
        clearFormErrors(form);

        const message = form.querySelector('[name="message"]');

        if (!message || message.value.trim() === '') {
            showError(message, 'Feedback message cannot be empty.');
            isValid = false;
        } else if (message.value.trim().length > 100) {
            showError(message, 'Feedback message cannot exceed 100 characters.');
            isValid = false;
        }

        if (!isValid) {
            e.preventDefault();
        }
    });
}

/**
 * Validates Advice Form
 * Database constraints:
 * - message: VARCHAR(200)
 * - user_id: VARCHAR(30)
 */
function bindAdviceFormValidation() {
    const form = document.getElementById('adviceForm');
    if (!form) return;

    form.addEventListener('submit', function (e) {
        let isValid = true;
        clearFormErrors(form);

        const userId = form.querySelector('[name="user_id"]');
        const message = form.querySelector('[name="message"]');

        if (!userId || userId.value.trim() === '') {
            showError(userId, 'Target User ID is required.');
            isValid = false;
        }

        if (!message || message.value.trim() === '') {
            showError(message, 'Advice message cannot be empty.');
            isValid = false;
        } else if (message.value.trim().length > 200) {
            showError(message, 'Advice message cannot exceed 200 characters.');
            isValid = false;
        }

        if (!isValid) {
            e.preventDefault();
        }
    });
}

/* --- Validation Utilities --- */

function validateEmail(email) {
    const re = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    return re.test(String(email).toLowerCase());
}

function showError(inputElement, errorMessage) {
    if (!inputElement) return;
    const formGroup = inputElement.closest('.form-group');
    if (formGroup) {
        formGroup.classList.add('has-error');
        let errorSpan = formGroup.querySelector('.form-error');
        if (!errorSpan) {
            errorSpan = document.createElement('span');
            errorSpan.className = 'form-error';
            formGroup.appendChild(errorSpan);
        }
        errorSpan.textContent = errorMessage;
    }
}

function clearFormErrors(form) {
    const errorGroups = form.querySelectorAll('.form-group.has-error');
    errorGroups.forEach(group => group.classList.remove('has-error'));
}
