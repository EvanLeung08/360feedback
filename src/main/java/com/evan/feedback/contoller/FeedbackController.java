package com.evan.feedback.controller;

import com.evan.feedback.contoller.BaseController;
import com.evan.feedback.model.Feedback;
import com.evan.feedback.model.User;
import com.evan.feedback.service.FeedbackService;
import com.evan.feedback.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/feedback")
public class FeedbackController extends BaseController {
    @Autowired
    private FeedbackService feedbackService;

    @Autowired
    private UserService userService;


    @GetMapping
    public String getAllFeedback(Model model) {
        List<Feedback> feedbacks = feedbackService.getAllFeedback();
        List<User> users = userService.getAllUsers();
        List<String> dimensions = List.of("Quality", "Timeliness", "Communication"); // Example dimensions

        model.addAttribute("feedbacks", feedbacks);
        model.addAttribute("users", users);
        model.addAttribute("dimensions", dimensions);

        return "feedback/feedback";
    }

    @PostMapping("/submit")
    public String submitFeedback(@RequestParam Long reviewerId, @RequestParam Long revieweeId, @RequestParam Map<String, String> dimensions) {
        User reviewer = userService.getUserById(reviewerId);
        User reviewee = userService.getUserById(revieweeId);

        dimensions.forEach((key, value) -> {
            if (key.endsWith(".dimension")) {
                String dimension = value;
                int score = Integer.parseInt(dimensions.get(key.replace(".dimension", ".score")));
                Feedback feedback = new Feedback();
                feedback.setDimension(dimension);
                feedback.setScore(score);
                feedback.setReviewer(reviewer);
                feedback.setReviewee(reviewee);
                feedbackService.submitFeedback(feedback, reviewerId, revieweeId);
            }
        });

        return "redirect:/feedback";
    }

    @GetMapping("/user/{userId}")
    public String getFeedbackForUser(@PathVariable Long userId, Model model) {
        List<Feedback> feedbacks = feedbackService.getFeedbackForUser(userId);
        model.addAttribute("feedbacks", feedbacks);
        return "users/user";
    }

    @GetMapping("/reviewer/{reviewerId}")
    public String getFeedbackByReviewer(@PathVariable Long reviewerId, Model model) {
        List<Feedback> feedbacks = feedbackService.getFeedbackByReviewer(reviewerId);
        model.addAttribute("feedbacks", feedbacks);
        return "feedback/reviewer";
    }
}