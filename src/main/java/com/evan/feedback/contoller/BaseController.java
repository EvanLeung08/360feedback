package com.evan.feedback.contoller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;

import jakarta.servlet.http.HttpServletRequest;

@Controller
public class BaseController {

    @ModelAttribute
    public void addUserInfoToModel(Model model, HttpServletRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userInfo;
        if (authentication != null && authentication.isAuthenticated() && !authentication.getName().equals("anonymousUser")) {
            userInfo = "Logged in as: " + authentication.getName();
        } else {
            String ipAddress = request.getRemoteAddr();
            userInfo = "Guest (IP: " + ipAddress + ")";
        }
        model.addAttribute("userInfo", userInfo);
    }
}