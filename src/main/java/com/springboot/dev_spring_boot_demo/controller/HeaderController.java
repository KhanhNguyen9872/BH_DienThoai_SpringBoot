package com.springboot.dev_spring_boot_demo.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.springboot.dev_spring_boot_demo.entity.*;
import com.springboot.dev_spring_boot_demo.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

@Controller
public class HeaderController {
    private final AdminService adminService;
    private final NotificationService notificationService;

    @Autowired
    public HeaderController(AdminService adminService, NotificationService notificationService) {
        this.adminService = adminService;
        this.notificationService = notificationService;
    }

    public void addHeaderDataToModel(UserDetails userDetails, Model model) {
        Admin admin = adminService.findByUsername(userDetails != null ? userDetails.getUsername() : "Anonymous");
        List<Notification> notifications = notificationService.findAll();
        notifications.sort(Comparator.comparing(Notification::getTime, Comparator.reverseOrder()));
        model.addAttribute("currentUser", admin);
        model.addAttribute("notifications", notifications);
    }
}
