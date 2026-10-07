# Online Personal Finance Tracker

A Java-based web application for managing personal finances through expenses, budgets, reports, advisor guidance, and feedback.

The application follows a layered architecture using JSP, Jakarta Servlets, Service classes, DAO classes, JDBC, and MySQL. Access is separated into three roles: **User, Advisor, and Admin**.

## 1. Project Overview

### User
- Register and log in
- Add, view, update, and delete expenses
- Create and manage budgets
- View spending summaries and recent activity
- Generate filtered financial reports
- View budget-versus-actual spending
- View financial advice
- Manage profile/password
- Submit feedback

### Advisor
- Log in through an advisor account
- View users available through the advisor interface
- View selected users' expense information
- Provide financial advice
- View and manage advice records

### Admin
- Log in through an admin account
- View and manage user accounts
- View submitted feedback
- Update feedback status
- View system-level statistics

## 2. Technology Stack

| Layer | Technology |
|---|---|
| Language | Java |
| Frontend | JSP, HTML, CSS, JavaScript, JSTL |
| Web Layer | Jakarta Servlets, Servlet Filters |
| Business Layer | Java Service Classes |
| Data Access | DAO Pattern + JDBC |
| Database | MySQL 8.x |
| Build Tool | Maven |
| Server | Apache Tomcat 11 |
| Java | JDK 21+ |
| Packaging | WAR |

The Maven project is compiled with Java release **21**, so JDK 21 or newer is recommended.

## 3. Architecture

```text
Browser
   |
   v
JSP / HTML / CSS / JavaScript
   |
   | HTTP Request
   v
Servlet
   |
   v
Service Layer
   |
   v
DAO Layer
   |
   v
JDBC
   |
   v
MySQL
```

The response follows the reverse path:

```text
MySQL → DAO → Service → Servlet → JSP → Browser
```

**JSP / Frontend** handles presentation and user interaction.

**Servlets** handle HTTP requests and responses.

**Services** handle validation and business logic.

**DAOs** handle SQL and database operations.

**Models** represent application data.

**Filters** handle authentication and role-based access.

## 4. Main Features

### Authentication and Authorization
- Registration and login
- Session-based authentication
- Logout
- Role-based access for User, Advisor, and Admin
- Protected routes using servlet filters

### Expense Management
- Add, view, update, and delete expenses
- Category-wise spending information

### Budget Management
- Create budgets by category and period
- View, update, and delete budgets
- Calculate spending against budgets
- Display used and remaining budget information

### Reports
- Date/category filtering
- Total spending
- Average expense
- Category-wise spending
- Budget-versus-actual comparison

The reporting module uses `ExecutorService`, `Callable`, and `Future` for independent report calculations.

### Advisor Module
- View available users
- View selected users' expense information
- Send financial advice
- View/manage advice records

### Admin Module
- View users
- Manage user accounts
- View feedback
- Update feedback status
- View application statistics

### Feedback
- Users can submit feedback
- Administrators can review feedback
- Feedback status can be updated

## 5. Authentication and Sessions

After successful login, the authenticated `User` object is stored in an `HttpSession`.

Two filters protect application routes:

- `AuthFilter` checks whether the user is logged in.
- `RoleFilter` checks whether the user's role is allowed to access the requested route.

```text
/user/*       → authenticated users
/advisor/*    → ADVISOR / ADMIN
/admin/*      → ADMIN
```

Relevant files:

```text
src/main/java/com/finance/filter/AuthFilter.java
src/main/java/com/finance/filter/RoleFilter.java
src/main/java/com/finance/servlet/auth/LoginServlet.java
```

## 6. Database

Database name:

```text
suspicious4
```

Tables:

```text
user
expenses
budgets
advice
feedback
```

The schema contains primary keys and foreign-key relationships between users and related records.

Database setup:

```text
database/schema.sql
```

The schema is structure-only and does not contain sample/demo records.

## 7. JDBC and CRUD

Database operations are implemented through JDBC and the DAO layer.

```text
Connection
    ↓
PreparedStatement
    ↓
executeQuery() / executeUpdate()
    ↓
ResultSet / affected rows
    ↓
Java model objects
```

The application uses parameterized `PreparedStatement` queries.

DAO classes:

```text
src/main/java/com/finance/dao/
├── AdviceDAO.java
├── BudgetDAO.java
├── ExpenseDAO.java
├── FeedbackDAO.java
└── UserDAO.java
```

Database connections are centralized in:

```text
src/main/java/com/finance/DBConnection.java
```

CRUD operations are implemented across users, expenses, budgets, advice, and feedback.

## 8. Exception Handling

The project uses application-specific exceptions:

```text
FinanceTrackerException
├── AuthenticationException
├── AuthorizationException
├── DatabaseException
└── ValidationException
```

Validation failures are represented by `ValidationException`, while database failures are handled through `DatabaseException`.

The DAO layer handles SQL exceptions and the servlet layer presents appropriate error messages to the user.

## 9. Transactions

User deletion is handled as a JDBC transaction.

```text
BEGIN TRANSACTION
       ↓
Delete related records
       ↓
Delete user
       ↓
All successful?
     /       \
   YES        NO
    ↓          ↓
 COMMIT     ROLLBACK
```

The implementation uses:

```text
setAutoCommit(false)
commit()
rollback()
```

Main implementation:

```text
src/main/java/com/finance/dao/UserDAO.java
```

## 10. Multithreading

`ReportService` demonstrates Java concurrency using:

- `ExecutorService`
- `Callable`
- `Future`

Three independent calculations are submitted to a fixed thread pool:

```text
                 ReportService
                      |
          +-----------+-----------+
          |           |           |
          v           v           v
       Summary     Category    Budget vs
       Report      Analysis     Actual
          |           |           |
          +-----------+-----------+
                      |
                      v
                 Report Results
```

The tasks calculate:

1. Total spending and average expense
2. Category-wise spending and percentages
3. Budget-versus-actual comparison

Implementation:

```text
src/main/java/com/finance/service/ReportService.java
```

## 11. Project Structure

```text
Online-Personal-Finance-Tracker/
│
├── database/
│   └── schema.sql
│
├── docs/
│   └── frontend-backend-contract.md
│
├── src/
│   └── main/
│       ├── java/com/finance/
│       │   ├── dao/
│       │   ├── exception/
│       │   ├── filter/
│       │   ├── model/
│       │   ├── service/
│       │   ├── servlet/
│       │   └── util/
│       │
│       ├── resources/
│       │   └── db.properties
│       │
│       └── webapp/
│           ├── WEB-INF/
│           │   └── views/
│           ├── assets/
│           └── index.jsp
│
├── db.properties.example
├── pom.xml
├── README.md
└── .gitignore
```

`db.properties` contains local credentials and is excluded from Git.

# 12. Local Setup

## Requirements

- JDK 21 or newer
- Maven
- MySQL 8.x
- Apache Tomcat 11
- Git

## Step 1 — Clone

```bash
git clone https://github.com/shreybaba/Online-Personal-Finance-Tracker.git
cd Online-Personal-Finance-Tracker
```

## Step 2 — Create the database

Run:

```text
database/schema.sql
```

This creates/uses:

```text
suspicious4
```

## Step 3 — Configure database credentials

Create:

```text
src/main/resources/db.properties
```

Use `db.properties.example` as the reference:

```properties
db.url=jdbc:mysql://localhost:3306/suspicious4
db.user=root
db.password=YOUR_MYSQL_PASSWORD
```

Do not commit `db.properties`. It is excluded through `.gitignore`.

## Step 4 — Build

From the project root:

```bash
mvn package
```

The build produces:

```text
target/finance.war
```

## Step 5 — Deploy to Tomcat

Copy:

```text
target/finance.war
```

to:

```text
<TOMCAT_HOME>/webapps/
```

Example:

```text
C:\apache-tomcat-11.x.x\webapps\finance.war
```

## Step 6 — Start Tomcat

### Windows

Open:

```text
<TOMCAT_HOME>\bin\
```

and run:

```text
startup.bat
```

### Git Bash

```bash
cd "<TOMCAT_HOME>/bin"
./startup.bat
```

## Step 7 — Open the application

```text
http://localhost:8080/finance/
```

The WAR is named `finance.war`, so the application context is `/finance`.

### Stop Tomcat

```text
<TOMCAT_HOME>\bin\shutdown.bat
```

## 13. Repository Safety

### Safe to commit

```text
db.properties.example
```

It contains placeholders only:

```properties
db.url=jdbc:mysql://localhost:3306/suspicious4
db.user=root
db.password=YOUR_MYSQL_PASSWORD_HERE
```

### Do not commit

```text
src/main/resources/db.properties
```

It contains local database credentials and is excluded by `.gitignore`.

Generated files, IDE metadata, logs, and other local development files are also excluded.

## 14. Review 1 Coverage

| Review 1 Requirement | Implementation |
|---|---|
| Problem Understanding & Solution Design | Role-based personal finance management system |
| OOP | Models, services, DAOs, inheritance, encapsulation |
| Collections | `List`, `ArrayList`, `Map`, `HashMap` |
| Exception Handling | Custom exception hierarchy and layered handling |
| Threads | `ExecutorService`, `Callable`, `Future` in `ReportService` |
| Database Schema | Five-table MySQL schema with relationships |
| JDBC | `Connection`, `PreparedStatement`, `ResultSet` |
| CRUD | Users, expenses, budgets, advice, feedback |
| Transactions | JDBC commit/rollback in `UserDAO` |
| HTTP | Servlets, GET/POST, parameters, forwards, redirects |
| Sessions | `HttpSession`, `AuthFilter`, `RoleFilter` |

### Key files

```text
OOP / Models
src/main/java/com/finance/model/

Exception Handling
src/main/java/com/finance/exception/

JDBC
src/main/java/com/finance/DBConnection.java
src/main/java/com/finance/dao/

Multithreading
src/main/java/com/finance/service/ReportService.java

Transactions
src/main/java/com/finance/dao/UserDAO.java

HTTP / Sessions
src/main/java/com/finance/servlet/
src/main/java/com/finance/filter/
```

## 15. Error Pages

The application includes dedicated pages for:

```text
403 — Access Denied
404 — Page Not Found
500 — Internal Server Error
```

Configured through:

```text
src/main/webapp/WEB-INF/web.xml
```

## 16. Build Output

Maven generates:

```text
target/finance.war
```

After deployment to Tomcat:

```text
http://localhost:8080/finance/
```

## 17. Repository

GitHub:

https://github.com/shreybaba/Online-Personal-Finance-Tracker

The repository contains the application source code, database schema, configuration template, documentation, and files required to understand and run the project locally.

Tip: Keep a copy of db.properties out of version control to protect your credentials.
