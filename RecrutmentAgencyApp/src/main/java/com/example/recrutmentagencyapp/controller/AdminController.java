package com.example.recrutmentagencyapp.controller;

import com.example.recrutmentagencyapp.entity.RecruitmentAgencyUtils;
import com.example.recrutmentagencyapp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {
    private UserService userService;

    @Autowired
    public AdminController(com.example.recrutmentagencyapp.service.UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/adminHomePage")
    public String getAdminHomePage(Model model) {
        var principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = RecruitmentAgencyUtils.getUsernameFromUserData(principal.toString());

        var currentUser = userService.getUserByUsername(username);
        model.addAttribute("user", currentUser);
        return "admin/admin-home";
    }

    @PostMapping("/createReport")
    public String createReportForLastMonth(Model model) {
        model.addAttribute("reportForMonth", userService.createReport());
        return "admin/report";
    }
}
