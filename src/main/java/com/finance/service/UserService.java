package com.finance.service;

import com.finance.dao.UserDAO;
import com.finance.exception.AuthenticationException;
import com.finance.exception.DatabaseException;
import com.finance.exception.ValidationException;
import com.finance.model.User;
import com.finance.util.IDGenerator;
import com.finance.util.ValidationUtil;

import java.util.List;

/**
 * Service class handling user authentication, profile management, and account administration.
 */
public class UserService {

    private final UserDAO userDAO;

    public UserService() {
        this.userDAO = new UserDAO();
    }

    // Constructor injection for testing
    public UserService(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    /**
     * Authenticates a user using email and password credentials.
     */
    public User login(String email, String password) throws ValidationException, AuthenticationException, DatabaseException {
        if (!ValidationUtil.isValidEmail(email)) {
            throw new ValidationException("Please enter a valid email address.");
        }
        if (ValidationUtil.isEmpty(password)) {
            throw new ValidationException("Password cannot be empty.");
        }

        User user = userDAO.findByEmail(email.trim());
        if (user == null || !user.getPassword().equals(password)) {
            throw new AuthenticationException("Invalid email or password.");
        }

        return user;
    }

    /**
     * Registers a new user account in the system.
     */
    public User register(String name, String email, String password, String role) 
            throws ValidationException, DatabaseException {
        
        if (ValidationUtil.isEmpty(name) || name.trim().length() > 30) {
            throw new ValidationException("Full Name is required and must not exceed 30 characters.");
        }
        if (!ValidationUtil.isValidEmail(email)) {
            throw new ValidationException("Please enter a valid email address.");
        }
        if (!ValidationUtil.isValidPassword(password)) {
            throw new ValidationException("Password must be between 6 and 16 characters long.");
        }
        if (ValidationUtil.isEmpty(role) || 
           (!role.equals("USER") && !role.equals("ADVISOR") && !role.equals("ADMIN"))) {
            throw new ValidationException("Please select a valid role (USER, ADVISOR, or ADMIN).");
        }

        // Check for duplicate email
        User existingUser = userDAO.findByEmail(email.trim());
        if (existingUser != null) {
            throw new ValidationException("An account with this email address already exists.");
        }

        String userId = IDGenerator.generateId("USR");
        User newUser = new User(userId, name.trim(), role.trim(), password, email.trim());
        
        boolean created = userDAO.createUser(newUser);
        if (!created) {
            throw new DatabaseException("Could not create user account. Please try again.");
        }

        return newUser;
    }

    /**
     * Updates user password after validating current password.
     */
    public void changePassword(String userId, String oldPassword, String newPassword) 
            throws ValidationException, AuthenticationException, DatabaseException {
        
        if (ValidationUtil.isEmpty(userId)) {
            throw new ValidationException("User session expired. Please sign in again.");
        }
        if (ValidationUtil.isEmpty(oldPassword) || ValidationUtil.isEmpty(newPassword)) {
            throw new ValidationException("Current and new passwords are required.");
        }
        if (!ValidationUtil.isValidPassword(newPassword)) {
            throw new ValidationException("New password must be between 6 and 16 characters long.");
        }

        User user = userDAO.findById(userId);
        if (user == null) {
            throw new AuthenticationException("User account not found.");
        }
        if (!user.getPassword().equals(oldPassword)) {
            throw new AuthenticationException("Current password is incorrect.");
        }

        userDAO.updatePassword(userId, newPassword);
    }

    /**
     * Retrieves all users for admin management view.
     */
    public List<User> getAllUsers() throws DatabaseException {
        return userDAO.findAllUsers();
    }

    /**
     * Retrieves count of registered users for admin statistics.
     */
    public int getTotalUserCount() throws DatabaseException {
        return userDAO.countUsers();
    }

    /**
     * Deletes a user account and associated child data in a transactional cascade.
     */
    public boolean deleteUser(String userId) throws DatabaseException {
        return userDAO.deleteUserWithCascade(userId);
    }
}
