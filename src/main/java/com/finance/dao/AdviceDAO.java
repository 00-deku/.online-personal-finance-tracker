package com.finance.dao;

import com.finance.DBConnection;
import com.finance.exception.DatabaseException;
import com.finance.model.Advice;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object (DAO) for Advice management matching 'ADVICE' database schema.
 */
public class AdviceDAO {

    /**
     * Inserts new financial advice into the ADVICE table.
     */
    public boolean createAdvice(Advice advice) throws DatabaseException {
        String sql = "INSERT INTO ADVICE (id, advisor_id, message, date, user_id) VALUES (?, ?, ?, ?, ?)";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, advice.getId());
            ps.setString(2, advice.getAdvisorId());
            ps.setString(3, advice.getMessage());
            ps.setDate(4, advice.getDate());
            ps.setString(5, advice.getUserId());
            
            return ps.executeUpdate() > 0;
            
        } catch (SQLException e) {
            throw new DatabaseException("Failed to issue financial advice: " + e.getMessage(), e);
        }
    }

    /**
     * Deletes an advice record issued by a specific advisor.
     */
    public boolean deleteAdvice(String adviceId, String advisorId) throws DatabaseException {
        String sql = "DELETE FROM ADVICE WHERE id = ? AND advisor_id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, adviceId);
            ps.setString(2, advisorId);
            
            return ps.executeUpdate() > 0;
            
        } catch (SQLException e) {
            throw new DatabaseException("Failed to delete advice record: " + e.getMessage(), e);
        }
    }

    /**
     * Retrieves all advice recommendations issued to a specific target user.
     */
    public List<Advice> findByUserId(String userId) throws DatabaseException {
        String sql = "SELECT id, advisor_id, message, date, user_id FROM ADVICE WHERE user_id = ? ORDER BY date DESC";
        List<Advice> list = new ArrayList<>();
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, userId);
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(extractAdviceFromResultSet(rs));
                }
            }
            return list;
            
        } catch (SQLException e) {
            throw new DatabaseException("Error retrieving advice for user: " + e.getMessage(), e);
        }
    }

    /**
     * Retrieves all advice issued by a specific advisor.
     */
    public List<Advice> findByAdvisorId(String advisorId) throws DatabaseException {
        String sql = "SELECT id, advisor_id, message, date, user_id FROM ADVICE WHERE advisor_id = ? ORDER BY date DESC";
        List<Advice> list = new ArrayList<>();
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, advisorId);
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(extractAdviceFromResultSet(rs));
                }
            }
            return list;
            
        } catch (SQLException e) {
            throw new DatabaseException("Error retrieving advice by advisor: " + e.getMessage(), e);
        }
    }

    /**
     * Counts total advice entries issued by an advisor.
     */
    public int countAdviceByAdvisorId(String advisorId) throws DatabaseException {
        String sql = "SELECT COUNT(*) FROM ADVICE WHERE advisor_id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, advisorId);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
            return 0;
            
        } catch (SQLException e) {
            throw new DatabaseException("Error counting advice entries: " + e.getMessage(), e);
        }
    }

    /**
     * Counts unique client users advised by a specific advisor.
     */
    public int countUniqueUsersAdvised(String advisorId) throws DatabaseException {
        String sql = "SELECT COUNT(DISTINCT user_id) FROM ADVICE WHERE advisor_id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, advisorId);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
            return 0;
            
        } catch (SQLException e) {
            throw new DatabaseException("Error counting advised client count: " + e.getMessage(), e);
        }
    }

    private Advice extractAdviceFromResultSet(ResultSet rs) throws SQLException {
        Advice advice = new Advice();
        advice.setId(rs.getString("id"));
        advice.setAdvisorId(rs.getString("advisor_id"));
        advice.setMessage(rs.getString("message"));
        advice.setDate(rs.getDate("date"));
        advice.setUserId(rs.getString("user_id"));
        return advice;
    }
}
