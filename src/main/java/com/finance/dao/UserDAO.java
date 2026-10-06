package com.finance.dao;

import com.finance.DBConnection;
import com.finance.exception.DatabaseException;
import com.finance.model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object (DAO) for managing User entity database operations.
 * Demonstrates PreparedStatement usage, try-with-resources, and explicit JDBC transactions.
 */
public class UserDAO {

    /**
     * Inserts a new user record into the USER table.
     */
    public boolean createUser(User user) throws DatabaseException {
        String sql = "INSERT INTO USER (id, name, role, password, email) VALUES (?, ?, ?, ?, ?)";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, user.getId());
            ps.setString(2, user.getName());
            ps.setString(3, user.getRole());
            ps.setString(4, user.getPassword());
            ps.setString(5, user.getEmail());
            
            int rows = ps.executeUpdate();
            return rows > 0;
            
        } catch (SQLException e) {
            throw new DatabaseException("Failed to register user account: " + e.getMessage(), e);
        }
    }

    /**
     * Fetches a user record by email address (used during login authentication).
     */
    public User findByEmail(String email) throws DatabaseException {
        String sql = "SELECT id, name, role, password, email FROM USER WHERE email = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, email);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return extractUserFromResultSet(rs);
                }
            }
            return null;
            
        } catch (SQLException e) {
            throw new DatabaseException("Error retrieving user by email: " + e.getMessage(), e);
        }
    }

    /**
     * Fetches a user record by primary key ID.
     */
    public User findById(String id) throws DatabaseException {
        String sql = "SELECT id, name, role, password, email FROM USER WHERE id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, id);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return extractUserFromResultSet(rs);
                }
            }
            return null;
            
        } catch (SQLException e) {
            throw new DatabaseException("Error retrieving user by ID: " + e.getMessage(), e);
        }
    }

    /**
     * Retrieves all registered user accounts sorted by name.
     */
    public List<User> findAllUsers() throws DatabaseException {
        String sql = "SELECT id, name, role, password, email FROM USER ORDER BY name ASC";
        List<User> userList = new ArrayList<>();
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                userList.add(extractUserFromResultSet(rs));
            }
            return userList;
            
        } catch (SQLException e) {
            throw new DatabaseException("Error retrieving user account list: " + e.getMessage(), e);
        }
    }

    /**
     * Returns total count of registered users for Admin statistics.
     */
    public int countUsers() throws DatabaseException {
        String sql = "SELECT COUNT(*) FROM USER";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            if (rs.next()) {
                return rs.getInt(1);
            }
            return 0;
            
        } catch (SQLException e) {
            throw new DatabaseException("Error counting user accounts: " + e.getMessage(), e);
        }
    }

    /**
     * Updates user password for profile management.
     */
    public boolean updatePassword(String userId, String newPassword) throws DatabaseException {
        String sql = "UPDATE USER SET password = ? WHERE id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, newPassword);
            ps.setString(2, userId);
            
            return ps.executeUpdate() > 0;
            
        } catch (SQLException e) {
            throw new DatabaseException("Error updating user password: " + e.getMessage(), e);
        }
    }

    /**
     * Deletes a user account along with associated dependent records (expenses, budgets, advice, feedback).
     * Strictly demonstrates JDBC Transaction Handling using setAutoCommit(false), commit(), and rollback().
     */
    public boolean deleteUserWithCascade(String userId) throws DatabaseException {
        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            
            // Step 1: Disable auto-commit to begin a JDBC Transaction
            conn.setAutoCommit(false);
            
            // Delete dependent records first to maintain referential integrity
            try (PreparedStatement psExp = conn.prepareStatement("DELETE FROM EXPENSES WHERE user_id = ?")) {
                psExp.setString(1, userId);
                psExp.executeUpdate();
            }
            
            try (PreparedStatement psBgt = conn.prepareStatement("DELETE FROM BUDGETS WHERE user_id = ?")) {
                psBgt.setString(1, userId);
                psBgt.executeUpdate();
            }
            
            try (PreparedStatement psAdv = conn.prepareStatement("DELETE FROM ADVICE WHERE user_id = ? OR advisor_id = ?")) {
                psAdv.setString(1, userId);
                psAdv.setString(2, userId);
                psAdv.executeUpdate();
            }
            
            try (PreparedStatement psFbk = conn.prepareStatement("DELETE FROM FEEDBACK WHERE user_id = ?")) {
                psFbk.setString(1, userId);
                psFbk.executeUpdate();
            }
            
            // Step 2: Delete the user record itself
            int rowsDeleted;
            try (PreparedStatement psUser = conn.prepareStatement("DELETE FROM USER WHERE id = ?")) {
                psUser.setString(1, userId);
                rowsDeleted = psUser.executeUpdate();
            }
            
            // Step 3: Commit transaction if all steps succeed
            conn.commit();
            return rowsDeleted > 0;
            
        } catch (SQLException e) {
            // Step 4: Rollback transaction if any error occurs during deletion
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException rollbackEx) {
                    System.err.println("Transaction rollback failed: " + rollbackEx.getMessage());
                }
            }
            throw new DatabaseException("Transaction failed while deleting user account: " + e.getMessage(), e);
            
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    System.err.println("Error closing connection: " + e.getMessage());
                }
            }
        }
    }

    // Private helper method to extract User entity from ResultSet
    private User extractUserFromResultSet(ResultSet rs) throws SQLException {
        User user = new User();
        user.setId(rs.getString("id"));
        user.setName(rs.getString("name"));
        user.setRole(rs.getString("role"));
        user.setPassword(rs.getString("password"));
        user.setEmail(rs.getString("email"));
        return user;
    }
}
