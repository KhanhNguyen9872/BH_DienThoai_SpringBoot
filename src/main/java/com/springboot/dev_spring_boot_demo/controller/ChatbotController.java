package com.springboot.dev_spring_boot_demo.controller;

import com.springboot.dev_spring_boot_demo.entity.HistoryChatbot;
import com.springboot.dev_spring_boot_demo.entity.UserInfo;
import com.springboot.dev_spring_boot_demo.service.AdminService;
import com.springboot.dev_spring_boot_demo.service.HistoryChatbotService;
import com.springboot.dev_spring_boot_demo.service.NotificationService;
import com.springboot.dev_spring_boot_demo.service.SettingsService;
import com.springboot.dev_spring_boot_demo.service.UserInfoService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/chatbot")
public class ChatbotController extends HeaderController {

    private final UserInfoService userInfoService;
    private final HistoryChatbotService historyChatbotService;
    private final SettingsService settingsService;

    public ChatbotController(AdminService adminService,
                             NotificationService notificationService,
                             UserInfoService userInfoService,
                             HistoryChatbotService historyChatbotService,
                             SettingsService settingsService) {
        super(adminService, notificationService);
        this.userInfoService = userInfoService;
        this.historyChatbotService = historyChatbotService;
        this.settingsService = settingsService;
    }

    /**
     * Display a paginated list of users.
     */
    @GetMapping
    public String listUsers(@AuthenticationPrincipal UserDetails userDetails,
                            Pageable pageable, Model model) {
        addHeaderDataToModel(userDetails, model);
        Page<UserInfo> usersPage = userInfoService.findAll(pageable);
        model.addAttribute("usersPage", usersPage);
        return "chatbot"; // Thymeleaf template for listing users
    }

    /**
     * Display the chat history for a given user.
     * Retrieves avatar settings from SettingsService.
     */
    @GetMapping("/{id}")
    public String showChatHistory(@AuthenticationPrincipal UserDetails userDetails,
                                  @PathVariable("id") Long id, Model model) {
        addHeaderDataToModel(userDetails, model);

        // Retrieve settings and prepare avatar URLs from the /img/ folder.
        Map<String, String> settings = settingsService.getSettings();
        String chatbotAvatar = "";
        String userAvatar = "";
        if (settings.get("CHATBOT_AVATAR") != null && !settings.get("CHATBOT_AVATAR").isEmpty()) {
            chatbotAvatar = settings.get("CHATBOT_AVATAR");
        }
        if (settings.get("CHATBOT_USER_AVATAR") != null && !settings.get("CHATBOT_USER_AVATAR").isEmpty()) {
            userAvatar = settings.get("CHATBOT_USER_AVATAR");
        }

        // Retrieve all chat history records and filter for the given user ID.
        List<HistoryChatbot> allHistory = historyChatbotService.findAll();
        List<HistoryChatbot> userHistory = allHistory.stream()
                .filter(message -> message.getUser() != null && message.getUser().getId().equals(id))
                .sorted(Comparator.comparing(HistoryChatbot::getTime))
                .collect(Collectors.toList());

        // Replace double newline characters with HTML <br> tags.
        userHistory.forEach(item -> {
            if (item.getMessage() != null) {
                item.setMessage(item.getMessage().replace("\n\n", "<br>"));
            }
        });

        model.addAttribute("id", id);
        model.addAttribute("history", userHistory);
        model.addAttribute("chatbotAvatar", chatbotAvatar);
        model.addAttribute("userAvatar", userAvatar);
        return "chatbot/show"; // Thymeleaf template for displaying a user's chat history
    }

    /**
     * Clear all chat history for the specified user.
     *
     * This method retrieves all history records, filters for those belonging to the user,
     * deletes each record, and then redirects back with a success message.
     */
    @PostMapping("/{id}/clear")
    public String clearChatHistory(@AuthenticationPrincipal UserDetails userDetails,
                                   @PathVariable("id") Long id,
                                   RedirectAttributes redirectAttributes) {
        // Retrieve all chat history for the user.
        List<HistoryChatbot> allHistory = historyChatbotService.findAll();
        List<HistoryChatbot> userHistory = allHistory.stream()
                .filter(message -> message.getUser() != null && message.getUser().getId().equals(id))
                .collect(Collectors.toList());

        // Delete each chat history record for the user.
        userHistory.forEach(message -> historyChatbotService.deleteById(message.getId()));

        redirectAttributes.addFlashAttribute("success", "Lịch sử trò chuyện đã được xóa thành công.");
        return "redirect:/chatbot";
    }
}
