# Online Personal Finance Tracker

A web-based personal finance management system developed as a college project.  
The application helps users manage their expenses and budgets, view financial reports, receive financial advice, and submit feedback.

## Tech Stack

- Java 26
- JSP
- Jakarta Servlets
- JDBC
- MySQL
- Maven
- Apache Tomcat 11
- HTML, CSS and JavaScript

## Features

### User
- Register and log in
- Manage personal expenses
- Add, edit and delete expenses
- Manage budgets
- View financial reports
- View profile information
- View financial advice
- Submit feedback

### Advisor
- Advisor login
- View registered users
- View user-related expense information
- Provide financial advice

### Admin
- Admin login
- View and manage users
- View feedback
- Manage feedback status
- View system-level information

## Project Architecture

The application follows a layered architecture:

```text
JSP / Frontend
      ↓
   Servlets
      ↓
   Services
      ↓
     DAO
      ↓
     JDBC
      ↓
    MySQL

## Multithreading

Dashboards and reports load their data in parallel on one shared worker pool (`com.finance.concurrent`):

- `TaskExecutor` – app-wide bounded `ThreadPoolExecutor` (`finance-worker-N` threads, `CallerRunsPolicy` back-pressure)
- `ParallelBatch` – forks a request's independent queries as `CompletableFuture`s and waits for them once
- `ReportService` – two phases: parallel DB reads, then parallel calculations chained with `thenCompose`/`thenCombine`
- `AppLifecycleListener` – starts/stops the pool and a `ScheduledExecutorService` that samples pool activity
- `ConcurrencyMonitor` – lock-free stats shown on `/docs` (live via `/api/concurrency`)

Each dashboard has a **Parallel execution** panel showing which thread ran each query and how long it took. Full explanation: `/docs`.
