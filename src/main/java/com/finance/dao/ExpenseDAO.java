package com.finance.dao;

import com.finance.DBConnection;
import com.finance.exception.DatabaseException;
import com.finance.model.Expense;

import java.math.BigDecimal;
import java.sql.Connection;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Data Access Object (DAO) for Expense management matching 'EXPENSES' database schema.
 */
public class ExpenseDAO {

    /**
     * Inserts a new expense transaction into the EXPENSES table.
     */
    public boolean createExpense(Expense expense) throws DatabaseException {
        String sql = "INSERT INTO EXPENSES (id, category, amount, date, user_id) VALUES (?, ?, ?, ?, ?)";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, expense.getId());
            ps.setString(2, expense.getCategory());
            ps.setBigDecimal(3, expense.getAmount());
            ps.setDate(4, expense.getDate());
            ps.setString(5, expense.getUserId());
            
            return ps.executeUpdate() > 0;
            
        } catch (SQLException e) {
            throw new DatabaseException("Failed to save expense record: " + e.getMessage(), e);
        }
    }

    /**
     * Updates an existing expense transaction.
     */
    public boolean updateExpense(Expense expense) throws DatabaseException {
        String sql = "UPDATE EXPENSES SET category = ?, amount = ?, date = ? WHERE id = ? AND user_id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, expense.getCategory());
            ps.setBigDecimal(2, expense.getAmount());
            ps.setDate(3, expense.getDate());
            ps.setString(4, expense.getId());
            ps.setString(5, expense.getUserId());
            
            return ps.executeUpdate() > 0;
            
        } catch (SQLException e) {
            throw new DatabaseException("Failed to update expense record: " + e.getMessage(), e);
        }
    }

    /**
     * Deletes an expense item by ID ensuring user ownership.
     */
    public boolean deleteExpense(String expenseId, String userId) throws DatabaseException {
        String sql = "DELETE FROM EXPENSES WHERE id = ? AND user_id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, expenseId);
            ps.setString(2, userId);
            
            return ps.executeUpdate() > 0;
            
        } catch (SQLException e) {
            throw new DatabaseException("Failed to delete expense record: " + e.getMessage(), e);
        }
    }

    /**
     * Finds a single expense by ID.
     */
    public Expense findById(String id) throws DatabaseException {
        String sql = "SELECT id, category, amount, date, user_id FROM EXPENSES WHERE id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, id);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return extractExpenseFromResultSet(rs);
                }
            }
            return null;
            
        } catch (SQLException e) {
            throw new DatabaseException("Error retrieving expense details: " + e.getMessage(), e);
        }
    }

    /**
     * Retrieves all expenses for a specific user ordered by date descending.
     */
    public List<Expense> findByUserId(String userId) throws DatabaseException {
        String sql = "SELECT id, category, amount, date, user_id FROM EXPENSES WHERE user_id = ? ORDER BY date DESC";
        List<Expense> list = new ArrayList<>();
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, userId);
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(extractExpenseFromResultSet(rs));
                }
            }
            return list;
            
        } catch (SQLException e) {
            throw new DatabaseException("Error retrieving user expenses: " + e.getMessage(), e);
        }
    }

    /**
     * Retrieves recent expenses limited by a specified count (used for dashboard).
     */
    public List<Expense> findRecentByUserId(String userId, int limit) throws DatabaseException {
        String sql = "SELECT id, category, amount, date, user_id FROM EXPENSES WHERE user_id = ? ORDER BY date DESC LIMIT ?";
        List<Expense> list = new ArrayList<>();
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, userId);
            ps.setInt(2, limit);
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(extractExpenseFromResultSet(rs));
                }
            }
            return list;
            
        } catch (SQLException e) {
            throw new DatabaseException("Error retrieving recent expenses: " + e.getMessage(), e);
        }
    }

    /**
     * Computes the total aggregate spending sum for a user.
     */
    public BigDecimal getTotalExpensesByUserId(String userId) throws DatabaseException {
        String sql = "SELECT SUM(amount) FROM EXPENSES WHERE user_id = ?";
        
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
            throw new DatabaseException("Error calculating total user expenses: " + e.getMessage(), e);
        }
    }

    /**
     * Computes spending sum grouped by category for a user.
     */
    public Map<String, BigDecimal> getCategoryTotalsByUserId(String userId) throws DatabaseException {
        String sql = "SELECT category, SUM(amount) as cat_total FROM EXPENSES WHERE user_id = ? GROUP BY category";
        Map<String, BigDecimal> categoryMap = new HashMap<>();
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, userId);
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    categoryMap.put(rs.getString("category"), rs.getBigDecimal("cat_total"));
                }
            }
            return categoryMap;
            
        } catch (SQLException e) {
            throw new DatabaseException("Error aggregating category totals: " + e.getMessage(), e);
        }
    }

    /**
     * Retrieves expenses for reports based on optional filters (date range, category).
     */
    public List<Expense> findByFilter(String userId, java.sql.Date startDate, java.sql.Date endDate, String category) throws DatabaseException {
        StringBuilder sql = new StringBuilder("SELECT id, category, amount, date, user_id FROM EXPENSES WHERE user_id = ?");
        List<Object> params = new ArrayList<>();
        params.add(userId);

        if (startDate != null) {
            sql.append(" AND date >= ?");
            params.add(startDate);
        }
        if (endDate != null) {
            sql.append(" AND date <= ?");
            params.add(endDate);
        }
        if (category != null && !category.trim().isEmpty()) {
            sql.append(" AND category = ?");
            params.add(category.trim());
        }

        sql.append(" ORDER BY date DESC");

        List<Expense> list = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {

            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(extractExpenseFromResultSet(rs));
                }
            }
            return list;

        } catch (SQLException e) {
            throw new DatabaseException("Error filtering expense reports: " + e.getMessage(), e);
        }
    }

    /**
     * Returns total system expense records count for Admin dashboard.
     */
    public int countAllExpenses() throws DatabaseException {
        String sql = "SELECT COUNT(*) FROM EXPENSES";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            if (rs.next()) {
                return rs.getInt(1);
            }
            return 0;
            
        } catch (SQLException e) {
            throw new DatabaseException("Error counting system expenses: " + e.getMessage(), e);
        }
    }

    private Expense extractExpenseFromResultSet(ResultSet rs) throws SQLException {
        Expense expense = new Expense();
        expense.setId(rs.getString("id"));
        expense.setCategory(rs.getString("category"));
        expense.setAmount(rs.getBigDecimal("amount"));
        expense.setDate(rs.getDate("date"));
        expense.setUserId(rs.getString("user_id"));
        return expense;
    }
}
