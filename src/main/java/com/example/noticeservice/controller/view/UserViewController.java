package com.example.noticeservice.controller.view;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class UserViewController {

    @GetMapping("/login")
    public String loginForm() {
        return "user/login";
    }

    @GetMapping("/signup")
    public String signupFrom() {
        return "user/signup";
    }
}
