-- Database schema for the Online Personal Finance Tracker.
-- Run with: mysql -u <admin-user> < src/main/resources/schema.sql

CREATE DATABASE IF NOT EXISTS suspicious4;
USE suspicious4;

CREATE TABLE IF NOT EXISTS user (
    id       VARCHAR(30) PRIMARY KEY,
    name     VARCHAR(30) NOT NULL,
    role     VARCHAR(10) NOT NULL,
    password VARCHAR(16) NOT NULL,
    email    VARCHAR(50) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS expenses (
    id       VARCHAR(30) PRIMARY KEY,
    category VARCHAR(15) NOT NULL,
    amount   DECIMAL(10,2) NOT NULL,
    date     DATE NOT NULL,
    user_id  VARCHAR(30) NOT NULL,
    INDEX idx_expenses_user (user_id)
);

CREATE TABLE IF NOT EXISTS budgets (
    id       VARCHAR(30) PRIMARY KEY,
    user_id  VARCHAR(30) NOT NULL,
    category VARCHAR(20) NOT NULL,
    amount   DECIMAL(10,2) NOT NULL,
    period   VARCHAR(20) NOT NULL,
    INDEX idx_budgets_user (user_id)
);

CREATE TABLE IF NOT EXISTS advice (
    id         VARCHAR(30) PRIMARY KEY,
    advisor_id VARCHAR(30) NOT NULL,
    message    VARCHAR(200) NOT NULL,
    date       DATE NOT NULL,
    user_id    VARCHAR(30) NOT NULL,
    INDEX idx_advice_user (user_id),
    INDEX idx_advice_advisor (advisor_id)
);

CREATE TABLE IF NOT EXISTS feedback (
    id      VARCHAR(30) PRIMARY KEY,
    user_id VARCHAR(30) NOT NULL,
    message VARCHAR(100) NOT NULL,
    status  VARCHAR(100) NOT NULL,
    date    DATE NOT NULL
);
