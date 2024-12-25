package com.evan.feedback.repository;

import com.evan.feedback.model.Feedback;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface FeedbackRepository extends JpaRepository<Feedback, Long> {
    List<Feedback> findByReviewerId(Long reviewerId);
}