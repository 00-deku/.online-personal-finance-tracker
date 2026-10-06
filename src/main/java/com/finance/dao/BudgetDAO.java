package com.finance.dao;

import com.finance.DBConnection;
import com.finance.exception.DatabaseException;
import com.finance.model.Budget;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object (DAO) for Budget management matching 'BUDGETS' database schema.
 */
public class BudgetDAO {

    /**
     * Saves a new budget or updates an existing one for the same user and category.
     */
    public boolean saveOrUpdateBudget(Budget budget) throws DatabaseException {
        // Check if budget already exists for this user and category
        Budget existing = findByUserIdAndCategory(budget.getUserId(), budget.getCategory());
        
        if (existing != null) {
            String updateSql = "UPDATE BUDGETS SET amount = ?, period = ? WHERE id = ?";
            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement ps = conn.prepareStatement(updateSql)) {
                
                ps.setBigDecimal(1, budget.getAmount());
                ps.setString(2, budget.getPeriod());
                ps.setString(3, existing.getId());
                
                return ps.executeUpdate() > 0;
            } catch (SQLException e) {
                throw new DatabaseException("Failed to update budget limit: " + e.getMessage(), e);
            }
        } else {
            String insertSql = "INSERT INTO BUDGETS (id, user_id, category, amount, period) VALUES (?, ?, ?, ?, ?)";
            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement ps = conn.prepareStatement(insertSql)) {
                
                ps.setString(1, budget.getId());
                ps.setString(2, budget.getUserId());
                ps.setString(3, budget.getCategory());
                ps.setBigDecimal(4, budget.getAmount());
                ps.setString(5, budget.getPeriod());
                
                return ps.executeUpdate() > 0;
            } catch (SQLException e) {
                throw new DatabaseException("Failed to save budget limit: " + e.getMessage(), e);
            }
        }
    }

    /**
     * Deletes a budget by ID verifying user ownership.
     */
    public boolean deleteBudget(String budgetId, String userId) throws DatabaseException {
        String sql = "DELETE FROM BUDGETS WHERE id = ? AND user_id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, budgetId);
            ps.setString(2, userId);
            
            return ps.executeUpdate() > 0;
            
        } catch (SQLException e) {
            throw new DatabaseException("Failed to delete budget record: " + e.getMessage(), e);
        }
    }

    /**
     * Retrieves all active budgets defined by a specific user.
     */
    public List<Budget> findByUserId(String userId) throws DatabaseException {
        String sql = "SELECT id, user_id, category, amount, period FROM BUDGETS WHERE user_id = ? ORDER BY category ASC";
        List<Budget> list = new ArrayList<>();
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, userId);
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(extractBudgetFromResultSet(rs));
                }
            }
            return list;
            
        } catch (SQLException e) {
            throw new DatabaseException("Error retrieving user budgets: " + e.getMessage(), e);
        }
    }

    /**
     * Finds budget by user ID and category.
     */
    public Budget findByUserIdAndCategory(String userId, String category) throws DatabaseException {
        String sql = "SELECT id, user_id, category, amount, period FROM BUDGETS WHERE user_id = ? AND category = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, userId);
            ps.setString(2, category);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return extractBudgetFromResultSet(rs);
                }
            }
            return null;
            
        } catch (SQLException e) {
            throw new DatabaseException("Error checking budget by category: " + e.getMessage(), e);
        }
    }

    /**
     * Calculates total active budget sum for a user.
     */
    public BigDecimal getTotalBudgetByUserId(String userId) throws DatabaseException {
        String sql = "SELECT SUM(amount) FROM BUDGETS WHERE user_id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, userId);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    BigDecimal total = rs.getBigDecimal(1);
                    return total != null ? total : BigDecimal.ZERO;
                }
            }
            return BigDecimal.ZERO;
            
        } catch (SQLException e) {
            throw new DatabaseException("Error calculating total budget limit: " + e.getMessage(), e);
        }
    }

    private Budget extractBudgetFromResultSet(ResultSet rs) throws SQLException {
        Budget budget = new Budget();
        budget.setId(rs.getString("id"));
        budget.setUserId(rs.getString("user_id"));
        budget.setCategory(rs.getString("category"));
        budget.setAmount(rs.getBigDecimal("amount"));
        budget.setPeriod(rs.getString("period"));
        return budget;
    }
}
