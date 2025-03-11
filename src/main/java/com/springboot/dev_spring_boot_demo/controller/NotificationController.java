package com.springboot.dev_spring_boot_demo.controller;

import com.springboot.dev_spring_boot_demo.entity.Notification;
import com.springboot.dev_spring_boot_demo.service.AdminService;
import com.springboot.dev_spring_boot_demo.service.NotificationService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/notifications")
public class NotificationController extends HeaderController {

    private final NotificationService notificationService;

    public NotificationController(AdminService adminService, NotificationService notificationService) {
        super(adminService, notificationService);
        this.notificationService = notificationService;
    }

    /**
     * Display paginated notifications.
     */
    @GetMapping
    public String index(Model model,
                        @RequestParam(defaultValue = "0") int page,
                        @AuthenticationPrincipal UserDetails userDetails) {
        addHeaderDataToModel(userDetails, model);
        // Retrieve notifications paginated (50 per page, ordered descending by time)
        Page<Notification> notificationsPage = notificationService.findAll(PageRequest.of(page, 50));
        model.addAttribute("notificationsPage", notificationsPage);
        return "notifications"; // Corresponds to templates/notifications.html
    }

    /**
     * Mark a specific notification as read.
     */
    @PostMapping("/{id}/read")
    public String markAsRead(@PathVariable("id") Long id,
                             RedirectAttributes redirectAttributes) {
        boolean updated = notificationService.markAsRead(id);
        if (updated) {
            redirectAttributes.addFlashAttribute("success", "Đánh dấu đã đọc thành công [ID: " + id + "]");
        } else {
            redirectAttributes.addFlashAttribute("error", "Không tìm thấy thông báo với ID: " + id);
        }
        return "redirect:/notifications";
    }

    /**
     * Mark all notifications as read.
     */
    @PostMapping("/markAllAsRead")
    public String markAllAsRead(RedirectAttributes redirectAttributes) {
        notificationService.markAllAsRead();
        redirectAttributes.addFlashAttribute("success", "Tất cả thông báo đã được đánh dấu đã đọc!");
        return "redirect:/notifications";
    }
}
