package com.evan.feedback.contoller;

import com.evan.feedback.model.User;
import com.evan.feedback.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;

@Controller
@RequestMapping("/auth")
public class AuthController extends BaseController {
    @Autowired
    private AuthService authService;

    @PostMapping("/register")
    public User register(@RequestBody User user) {
        return authService.register(user);
    }

    @PostMapping("/login")
    public String login(@RequestParam String username, @RequestParam String password, Model model) {
        User user = authService.login(username, password);
        if (user != null) {
            return "redirect:/users/"+user.getId(); // 假设登录成功后重定向到主页
        } else {
            model.addAttribute("error", "Invalid username or password");
            return "auth/login";
        }
    }

    @GetMapping("/login")
    public String showLoginPage() {
        return "auth/login";
    }
}