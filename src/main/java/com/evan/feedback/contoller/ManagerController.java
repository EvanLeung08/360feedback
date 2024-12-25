package com.evan.feedback.contoller;

import com.evan.feedback.model.Feedback;
import com.evan.feedback.model.User;
import com.evan.feedback.service.FeedbackService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/manager")
public class ManagerController extends BaseController {

    @Autowired
    private FeedbackService feedbackService;

    @Autowired
    private ObjectMapper objectMapper;

    @GetMapping
    public String getManagerPage(Model model, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "0") int rankingPage) throws JsonProcessingException {
        // Fetch all feedback for ranking
        List<Feedback> allFeedback = feedbackService.getAllFeedback();
        Map<User, List<Feedback>> feedbacksByReviewee = allFeedback.stream().collect(Collectors.groupingBy(Feedback::getReviewee));

        Map<User, Map<String, Double>> totalScores = feedbacksByReviewee.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> entry.getValue().stream()
                                .collect(Collectors.groupingBy(
                                        Feedback::getDimension,
                                        Collectors.summingDouble(Feedback::getScore)
                                ))
                ));

        // Calculate average total scores and sort
        List<Map.Entry<User, Double>> ranking = totalScores.entrySet().stream()
                .map(entry -> Map.entry(entry.getKey(), entry.getValue().values().stream().mapToDouble(Double::doubleValue).average().orElse(0.0)))
                .sorted(Map.Entry.<User, Double>comparingByValue().reversed())
                .collect(Collectors.toList());

        // Pagination for ranking
        int rankingPageSize = 5;
        int rankingStart = rankingPage * rankingPageSize;
        int rankingEnd = Math.min(rankingStart + rankingPageSize, ranking.size());
        List<Map.Entry<User, Double>> paginatedRanking = ranking.subList(rankingStart, rankingEnd);

        // Fetch paginated feedback for display
        Page<Feedback> feedbackPage = feedbackService.getAllFeedback(PageRequest.of(page, 5));
        Map<User, List<Feedback>> paginatedFeedbacksByReviewee = feedbackPage.getContent().stream().collect(Collectors.groupingBy(Feedback::getReviewee));

        Map<User, Map<String, Double>> averageScores = paginatedFeedbacksByReviewee.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> entry.getValue().stream()
                                .collect(Collectors.groupingBy(
                                        Feedback::getDimension,
                                        Collectors.averagingDouble(Feedback::getScore)
                                ))
                ));

        model.addAttribute("feedbacksByReviewee", paginatedFeedbacksByReviewee);
        model.addAttribute("totalScores", totalScores);
        model.addAttribute("averageScores", averageScores);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", feedbackPage.getTotalPages());
        model.addAttribute("ranking", paginatedRanking); // Add paginated ranking data
        model.addAttribute("rankingPage", rankingPage);
        model.addAttribute("rankingTotalPages", (int) Math.ceil((double) ranking.size() / rankingPageSize));
        return "manager/manager";
    }
}