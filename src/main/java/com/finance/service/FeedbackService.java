package com.finance.service;

import com.finance.dao.FeedbackDAO;
import com.finance.exception.DatabaseException;
import com.finance.exception.ValidationException;
import com.finance.model.Feedback;
import com.finance.util.IDGenerator;
import com.finance.util.ValidationUtil;

import java.sql.Date;
import java.util.List;

/**
 * Service managing user feedback submissions and admin feedback queue status updates.
 */
public class FeedbackService {

    private final FeedbackDAO feedbackDAO;

    public FeedbackService() {
        this.feedbackDAO = new FeedbackDAO();
    }

    public FeedbackService(FeedbackDAO feedbackDAO) {
        this.feedbackDAO = feedbackDAO;
    }

    /**
     * Submits a user feedback ticket with default status 'PENDING'.
     */
    public void submitFeedback(String userId, String message) throws ValidationException, DatabaseException {
        if (ValidationUtil.isEmpty(userId)) {
            throw new ValidationException("User session expired.");
        }
        if (ValidationUtil.isEmpty(message) || message.trim().length() > 100) {
            throw new ValidationException("Feedback message is required and must not exceed 100 characters.");
        }

        String feedbackId = IDGenerator.generateId("FBK");
        Date currentDate = new Date(System.currentTimeMillis());
        
        Feedback feedback = new Feedback(feedbackId, userId.trim(), message.trim(), "PENDING", currentDate);
        feedbackDAO.createFeedback(feedback);
    }

    public List<Feedback> getAllFeedback() throws DatabaseException {
        return feedbackDAO.findAllFeedback();
    }

    public void updateStatus(String feedbackId, String newStatus) throws ValidationException, DatabaseException {
        if (ValidationUtil.isEmpty(feedbackId)) {
            throw new ValidationException("Feedback ID is required.");
        }
        if (ValidationUtil.isEmpty(newStatus) || 
           (!newStatus.equals("PENDING") && !newStatus.equals("IN_REVIEW") && !newStatus.equals("RESOLVED"))) {
            throw new ValidationException("Invalid feedback status value.");
        }

        feedbackDAO.updateStatus(feedbackId.trim(), newStatus.trim());
    }

    public int getPendingFeedbackCount() throws DatabaseException {
        return feedbackDAO.countPendingFeedback();
    }
}
