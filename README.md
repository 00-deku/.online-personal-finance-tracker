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
