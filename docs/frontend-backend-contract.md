# Frontend-Backend Integration Contract & Specification
**Project:** Online Personal Finance Management System (`Finance.`)  
**Target Architecture:** Java 21, Maven WAR, Jakarta Servlet 6.1, Tomcat 11, JSP, JDBC, MySQL (`sucius4`)  
**Context Path:** `/finance/`

---

## 1. Database Schema Reference (Source of Truth)

All frontend forms, tables, and attributes map strictly to these 5 tables in the `sucius4` database:

### `user` Table
| Column Name | Type | Constraints | Description |
|---|---|---|---|
| `id` | `VARCHAR(30)` | PRIMARY KEY | Unique user identifier |
| `name` | `VARCHAR(30)` | NOT NULL | Full name |
| `role` | `VARCHAR(10)` | NOT NULL | System role (`USER`, `ADVISOR`, `ADMIN`) |
| `password` | `VARCHAR(16)` | NOT NULL | Auth password (never displayed) |
| `email` | `VARCHAR(50)` | NOT NULL, UNIQUE | User email |

### `expenses` Table
| Column Name | Type | Constraints | Description |
|---|---|---|---|
| `id` | `VARCHAR(30)` | PRIMARY KEY | Unique expense ID |
| `category` | `VARCHAR(15)` | NOT NULL | Category name |
| `amount` | `DECIMAL(10,2)` | NOT NULL | Transaction amount |
| `date` | `DATE` | NOT NULL | Date (`YYYY-MM-DD`) |
| `user_id` | `VARCHAR(30)` | NOT NULL | Associated user ID |

### `budgets` Table
| Column Name | Type | Constraints | Description |
|---|---|---|---|
| `id` | `VARCHAR(30)` | PRIMARY KEY | Unique budget ID |
| `user_id` | `VARCHAR(30)` | NOT NULL | Associated user ID |
| `category` | `VARCHAR(20)` | NOT NULL | Category name |
| `amount` | `DECIMAL(10,2)` | NOT NULL | Target budget limit |
| `period` | `VARCHAR(20)` | NOT NULL | Period (`Monthly`, `Weekly`, `Annual`) |

### `advice` Table
| Column Name | Type | Constraints | Description |
|---|---|---|---|
| `id` | `VARCHAR(30)` | PRIMARY KEY | Unique advice ID |
| `advisor_id` | `VARCHAR(30)` | NOT NULL | Advisor user ID |
| `message` | `VARCHAR(200)` | NOT NULL | Advice message |
| `date` | `DATE` | NOT NULL | Issue date |
| `user_id` | `VARCHAR(30)` | NOT NULL | Target user ID |

### `feedback` Table
| Column Name | Type | Constraints | Description |
|---|---|---|---|
| `id` | `VARCHAR(30)` | PRIMARY KEY | Unique feedback ticket ID |
| `user_id` | `VARCHAR(30)` | NOT NULL | User ID |
| `message` | `VARCHAR(100)` | NOT NULL | User message |
| `status` | `VARCHAR(100)` | NOT NULL | Status (`PENDING`, `IN_REVIEW`, `RESOLVED`) |
| `date` | `DATE` | NOT NULL | Date |

---

## 2. Empty State & Data Contract

All JSP pages are configured to handle null or empty backend data collections gracefully without hardcoded sample data.

| View | Expected Attribute | Behavior when Empty / Null |
|---|---|---|
| `user/dashboard.jsp` | `totalExpenses`, `totalBudget`, `remainingBudget` | Displays `—` in stat cards |
| `user/dashboard.jsp` | `recentExpenses` | Displays `"No expenses yet. Add your first expense to start tracking your spending."` |
| `user/dashboard.jsp` | `categoryBreakdown` | Displays `"No spending data available. Category breakdown will appear once expenses are logged."` |
| `user/dashboard.jsp` | `activeBudget` | Displays `"No budgets available. Create a budget to start managing your spending limits."` |
| `user/dashboard.jsp` | `adviceList` | Displays `"No advice available. Your advisor hasn't added any advice yet."` |
| `user/expenses.jsp` | `expensesList` | Displays `"No expenses yet. Add your first expense to start tracking your spending."` |
| `user/budgets.jsp` | `budgetsList` | Displays `"No budgets available. Create a budget to start managing your spending limits."` |
| `user/reports.jsp` | `reportTotalSpending` | Displays `"No report data available. Reports will appear once sufficient expense data is available."` |
| `user/profile.jsp` | `sessionScope.user` | Displays `"User profile data unavailable. Session data will be loaded upon login."` |
| `advisor/dashboard.jsp` | `adviceList` | Displays `"No advice records available. Recommendations issued to users will appear here."` |
| `advisor/advice.jsp` | `adviceList` | Displays `"No advice available. Your advisor hasn't added any advice yet."` |
| `admin/users.jsp` | `userList` | Displays `"No registered users found. Registered user accounts will appear here."` |
| `admin/feedback.jsp` | `feedbackList` | Displays `"No feedback tickets available. Submitted user feedback will appear here."` |

---

## 3. Maven Build & Path Safety
All static assets and forms evaluate URLs dynamically:
```jsp
<link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
<script src="${pageContext.request.contextPath}/assets/js/main.js"></script>
<form action="${pageContext.request.contextPath}/login" method="post">
```
Packaging: Maven WAR (`target/finance.war`).
