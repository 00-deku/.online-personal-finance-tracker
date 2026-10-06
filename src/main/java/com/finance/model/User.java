package com.finance.model;

import java.io.Serializable;

/**
 * Encapsulates system user account data matching the 'USER' table schema.
 * Supports roles: USER, ADVISOR, ADMIN.
 */
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    private String id;
    private String name;
    private String role;
    private String password;
    private String email;

    // Default constructor required for JavaBeans
    public User() {
    }

    // Full constructor
    public User(String id, String name, String role, String password, String email) {
        this.id = id;
        this.name = name;
        this.role = role;
        this.password = password;
        this.email = email;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", role='" + role + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}
