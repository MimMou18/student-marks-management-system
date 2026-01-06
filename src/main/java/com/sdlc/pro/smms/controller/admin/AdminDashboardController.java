package com.sdlc.pro.smms.controller.admin;

import com.sdlc.pro.smms.entity.AppUser;
import com.sdlc.pro.smms.util.AuthUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminDashboardController {

    @GetMapping("/dashboard")
    public String dashboard() {
        return "admin/dashboard";
    }


}
