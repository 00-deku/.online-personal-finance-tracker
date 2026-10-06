package com.finance.model;

import java.io.Serializable;
import java.sql.Date;

/**
 * Encapsulates feedback or support tickets submitted by users matching the 'FEEDBACK' table schema.
 * Status can be PENDING, IN_REVIEW, or RESOLVED.
 */
public class Feedback implements Serializable {

    private static final long serialVersionUID = 1L;

    private String id;
    private String userId;
    private String message;
    private String status;
    private Date date;

    public Feedback() {
    }

    public Feedback(String id, String userId, String message, String status, Date date) {
        this.id = id;
        this.userId = userId;
        this.message = message;
        this.status = status;
        this.date = date;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public String getUser_id() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
