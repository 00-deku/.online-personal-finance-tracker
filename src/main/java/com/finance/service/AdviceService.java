package com.finance.service;

import com.finance.dao.AdviceDAO;
import com.finance.dao.UserDAO;
import com.finance.exception.DatabaseException;
import com.finance.exception.ValidationException;
import com.finance.model.Advice;
import com.finance.model.User;
import com.finance.util.IDGenerator;
import com.finance.util.ValidationUtil;

import java.sql.Date;
import java.util.List;

/**
 * Service managing financial advice recommendations issued by Advisors to target client users.
 */
public class AdviceService {

    private final AdviceDAO adviceDAO;
    private final UserDAO userDAO;

    public AdviceService() {
        this.adviceDAO = new AdviceDAO();
        this.userDAO = new UserDAO();
    }

    public AdviceService(AdviceDAO adviceDAO, UserDAO userDAO) {
        this.adviceDAO = adviceDAO;
        this.userDAO = userDAO;
    }

    /**
     * Issues new advice to a client user after validating target user existence.
     */
    public void sendAdvice(String advisorId, String targetUserId, String dateStr, String message) 
            throws ValidationException, DatabaseException {
        
        if (ValidationUtil.isEmpty(advisorId)) {
            throw new ValidationException("Advisor session expired.");
        }
        if (ValidationUtil.isEmpty(targetUserId)) {
            throw new ValidationException("Target User ID is required.");
        }
        if (ValidationUtil.isEmpty(dateStr)) {
            throw new ValidationException("Advice issue date is required.");
        }
        if (ValidationUtil.isEmpty(message) || message.trim().length() > 200) {
            throw new ValidationException("Advice message is required and must not exceed 200 characters.");
        }

        // Verify target user exists in database
        User targetUser = userDAO.findById(targetUserId.trim());
        if (targetUser == null) {
            throw new ValidationException("Target User with ID '" + targetUserId + "' does not exist.");
        }

        Date date;
        try {
            date = Date.valueOf(dateStr.trim());
        } catch (IllegalArgumentException e) {
            throw new ValidationException("Invalid date format. Expected YYYY-MM-DD.");
        }

        String adviceId = IDGenerator.generateId("ADV");
        Advice advice = new Advice(adviceId, advisorId, message.trim(), date, targetUserId.trim());
        
        adviceDAO.createAdvice(advice);
    }

    public void deleteAdvice(String adviceId, String advisorId) throws DatabaseException {
        adviceDAO.deleteAdvice(adviceId, advisorId);
    }

    public List<Advice> getAdviceForUser(String userId) throws DatabaseException {
        return adviceDAO.findByUserId(userId);
    }

    public List<Advice> getAdviceByAdvisor(String advisorId) throws DatabaseException {
        return adviceDAO.findByAdvisorId(advisorId);
    }

    public int getIssuedAdviceCount(String advisorId) throws DatabaseException {
        return adviceDAO.countAdviceByAdvisorId(advisorId);
    }

    public int getUniqueUsersAdvisedCount(String advisorId) throws DatabaseException {
        return adviceDAO.countUniqueUsersAdvised(advisorId);
    }
}
