package com.example.sk_pjt2_back_chat.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 얜 왜있냐?
 */

@Controller
@RequestMapping("/test")
public class TestController {
    @GetMapping("/")
    public String test() {
        return "test";
    }
}
