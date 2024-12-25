package com.evan.feedback.service;

import com.evan.feedback.model.Feedback;
import com.evan.feedback.model.User;
import com.evan.feedback.repository.FeedbackRepository;
import com.evan.feedback.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FeedbackService {

    @Autowired
    private FeedbackRepository feedbackRepository;

    @Autowired
    private UserRepository userRepository;

    public List<Feedback> getAllFeedback() {
        return feedbackRepository.findAll();
    }

    public void submitFeedback(Feedback feedback, Long reviewerId, Long revieweeId) {
        User reviewer = userRepository.findById(reviewerId).orElseThrow(() -> new IllegalArgumentException("Invalid reviewer ID"));
        User reviewee = userRepository.findById(revieweeId).orElseThrow(() -> new IllegalArgumentException("Invalid reviewee ID"));
        feedback.setReviewer(reviewer);
        feedback.setReviewee(reviewee);
        feedbackRepository.save(feedback);
    }

    public List<Feedback> getFeedbackForUser(Long userId) {
        return feedbackRepository.findByReviewerId(userId);
    }

    public List<Feedback> getFeedbackByReviewer(Long reviewerId) {
        return feedbackRepository.findByReviewerId(reviewerId);
    }

    public Page<Feedback> getAllFeedback(Pageable pageable) {
        return feedbackRepository.findAll(pageable);
    }
}