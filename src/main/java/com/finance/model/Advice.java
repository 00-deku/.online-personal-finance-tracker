package com.finance.model;

import java.io.Serializable;
import java.sql.Date;

/**
 * Represents financial advice issued by an Advisor to a User matching the 'ADVICE' table schema.
 */
public class Advice implements Serializable {

    private static final long serialVersionUID = 1L;

    private String id;
    private String advisorId;
    private String message;
    private Date date;
    private String userId;

    public Advice() {
    }

    public Advice(String id, String advisorId, String message, Date date, String userId) {
        this.id = id;
        this.advisorId = advisorId;
        this.message = message;
        this.date = date;
        this.userId = userId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAdvisorId() {
        return advisorId;
    }

    public String getAdvisor_id() {
        return advisorId;
    }

    public void setAdvisorId(String advisorId) {
        this.advisorId = advisorId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
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
}
