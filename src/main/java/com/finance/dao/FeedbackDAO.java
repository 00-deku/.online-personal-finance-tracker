package com.finance.dao;

import com.finance.DBConnection;
import com.finance.exception.DatabaseException;
import com.finance.model.Feedback;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object (DAO) for Feedback management matching 'FEEDBACK' database schema.
 */
public class FeedbackDAO {

    /**
     * Inserts a new feedback ticket submitted by a user.
     */
    public boolean createFeedback(Feedback feedback) throws DatabaseException {
        String sql = "INSERT INTO FEEDBACK (id, user_id, message, status, date) VALUES (?, ?, ?, ?, ?)";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, feedback.getId());
            ps.setString(2, feedback.getUserId());
            ps.setString(3, feedback.getMessage());
            ps.setString(4, feedback.getStatus());
            ps.setDate(5, feedback.getDate());
            
            return ps.executeUpdate() > 0;
            
        } catch (SQLException e) {
            throw new DatabaseException("Failed to submit feedback ticket: " + e.getMessage(), e);
        }
    }

    /**
     * Retrieves all feedback tickets sorted by date descending for Admin view.
     */
    public List<Feedback> findAllFeedback() throws DatabaseException {
        String sql = "SELECT id, user_id, message, status, date FROM FEEDBACK ORDER BY date DESC";
        List<Feedback> list = new ArrayList<>();
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                list.add(extractFeedbackFromResultSet(rs));
            }
            return list;
            
        } catch (SQLException e) {
            throw new DatabaseException("Error retrieving feedback queue: " + e.getMessage(), e);
        }
    }

    /**
     * Updates the review status of a feedback ticket (e.g. PENDING -> IN_REVIEW -> RESOLVED).
     */
    public boolean updateStatus(String feedbackId, String newStatus) throws DatabaseException {
        String sql = "UPDATE FEEDBACK SET status = ? WHERE id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, newStatus);
            ps.setString(2, feedbackId);
            
            return ps.executeUpdate() > 0;
            
        } catch (SQLException e) {
            throw new DatabaseException("Error updating feedback status: " + e.getMessage(), e);
        }
    }

    /**
     * Counts feedback tickets with 'PENDING' status for Admin overview.
     */
    public int countPendingFeedback() throws DatabaseException {
        String sql = "SELECT COUNT(*) FROM FEEDBACK WHERE status = 'PENDING'";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            if (rs.next()) {
                return rs.getInt(1);
            }
            return 0;
            
        } catch (SQLException e) {
            throw new DatabaseException("Error counting pending feedback tickets: " + e.getMessage(), e);
        }
    }

    private Feedback extractFeedbackFromResultSet(ResultSet rs) throws SQLException {
        Feedback feedback = new Feedback();
        feedback.setId(rs.getString("id"));
        feedback.setUserId(rs.getString("user_id"));
        feedback.setMessage(rs.getString("message"));
        feedback.setStatus(rs.getString("status"));
        feedback.setDate(rs.getDate("date"));
        return feedback;
    }
}
