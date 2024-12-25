package com.evan.feedback.contoller;

import com.evan.feedback.model.Feedback;
import com.evan.feedback.model.User;
import com.evan.feedback.service.FeedbackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/manager")
public class ManagerController extends BaseController {

    @Autowired
    private FeedbackService feedbackService;

    @GetMapping
    public String getManagerPage(Model model, @RequestParam(defaultValue = "0") int page) {
        Page<Feedback> feedbackPage = feedbackService.getAllFeedback(PageRequest.of(page, 5));
        Map<User, List<Feedback>> feedbacksByReviewee = feedbackPage.getContent().stream().collect(Collectors.groupingBy(Feedback::getReviewee));

        Map<User, Map<String, Double>> totalScores = feedbacksByReviewee.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> entry.getValue().stream()
                                .collect(Collectors.groupingBy(
                                        Feedback::getDimension,
                                        Collectors.summingDouble(Feedback::getScore)
                                ))
                ));

        Map<User, Map<String, Double>> averageScores = feedbacksByReviewee.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> entry.getValue().stream()
                                .collect(Collectors.groupingBy(
                                        Feedback::getDimension,
                                        Collectors.averagingDouble(Feedback::getScore)
                                ))
                ));



        model.addAttribute("feedbacksByReviewee", feedbacksByReviewee);
        model.addAttribute("totalScores", totalScores);
        model.addAttribute("averageScores", averageScores);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", feedbackPage.getTotalPages());
        return "manager/manager";
    }
}