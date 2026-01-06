package com.sdlc.pro.smms.controller.student;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/student")
public class StudentAuthController {

    @RequestMapping("/login")
    public String login(Model model) {
        model.addAttribute("heading", "Student Login");
        return "auth/login";
    }
}
