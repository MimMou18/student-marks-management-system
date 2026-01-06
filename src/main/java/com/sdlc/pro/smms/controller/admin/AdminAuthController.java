package com.sdlc.pro.smms.controller.admin;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
public class AdminAuthController {

    @RequestMapping("/login")
    public String login(Model model) {
        model.addAttribute("heading", "Admin Login");
        return "auth/login";
    }
}
