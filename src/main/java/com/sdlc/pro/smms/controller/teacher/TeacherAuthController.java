package com.sdlc.pro.smms.controller.teacher;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/teacher")
public class TeacherAuthController {

    @RequestMapping("/login")
    public String login(Model model) {
        model.addAttribute("heading", "Teacher Login");
        return "auth/login";
    }
}
