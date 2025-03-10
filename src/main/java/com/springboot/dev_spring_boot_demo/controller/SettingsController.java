package com.springboot.dev_spring_boot_demo.controller;

import com.springboot.dev_spring_boot_demo.service.AdminService;
import com.springboot.dev_spring_boot_demo.service.NotificationService;
import com.springboot.dev_spring_boot_demo.service.SettingsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.transaction.Transactional;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Controller
@RequestMapping("/settings")
public class SettingsController extends HeaderController {

    // Adjust this to your desired base directory for images
    private static final String IMG_DIR = "img";

    private final SettingsService settingsService;

    @Autowired
    public SettingsController(AdminService adminService, NotificationService notificationService, SettingsService settingsService) {
        super(adminService, notificationService);
        this.settingsService = settingsService;
    }

    @GetMapping
    public String index(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        addHeaderDataToModel(userDetails, model);
        // Retrieve all settings as a Map<String, String>
        Map<String, String> settings = settingsService.getSettings();
        model.addAttribute("settings", settings);
        return "settings";
    }

    @PostMapping("/update")
    @Transactional
    public String updateSettings(
            @RequestParam(value = "telegram_status", required = false) String telegramStatus,
            @RequestParam(value = "telegram_username", required = false) String telegramUsername,
            @RequestParam(value = "telegram_token", required = false) String telegramToken,
            @RequestParam(value = "telegram_chat_id", required = false) String telegramChatId,
            @RequestParam(value = "maintenance", required = false) String maintenance,
            @RequestParam(value = "chatbot_enable", required = false) String chatbotEnable,
            @RequestParam(value = "local_chatbot_model", required = false) String localChatbotModel,
            @RequestParam(value = "local_chatbot_url", required = false) String localChatbotUrl,
            @RequestParam(value = "local_chatbot_temperature", required = false) String localChatbotTemperature,
            @RequestParam(value = "gemini_api_key", required = false) String geminiApiKey,
            @RequestParam(value = "bot_avatar", required = false) MultipartFile botAvatar,
            @RequestParam(value = "user_avatar", required = false) MultipartFile userAvatar,
            RedirectAttributes redirectAttributes
    ) {
        // Validate mandatory fields for different chatbot modes
        if ("1".equals(chatbotEnable)) { // Local Chatbot mode
            if (localChatbotModel == null || localChatbotModel.trim().isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "Tên mô hình Chatbot không được để trống khi chọn chế độ [Cục bộ]");
                return "redirect:/settings";
            }
            if (localChatbotUrl == null || localChatbotUrl.trim().isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "URL Chatbot cục bộ không được để trống khi chọn chế độ [Cục bộ]");
                return "redirect:/settings";
            }
            if (localChatbotTemperature == null || localChatbotTemperature.trim().isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "Mức độ ngẫu nhiên Chatbot cục bộ không được để trống khi chọn chế độ [Cục bộ]");
                return "redirect:/settings";
            }
        } else if ("2".equals(chatbotEnable)) { // Gemini mode
            if (geminiApiKey == null || geminiApiKey.trim().isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "Gemini API Key không được để trống khi chọn chế độ [Gemini]");
                return "redirect:/settings";
            }
        }

        // Optional: You may validate that localChatbotTemperature is a valid number between 0.0 and 1.0.
        try {
            double temp = localChatbotTemperature != null ? Double.parseDouble(localChatbotTemperature) : 0.0;
            if (temp < 0.0 || temp > 1.0) {
                redirectAttributes.addFlashAttribute("error", "Temperature phải nằm trong khoảng từ 0.0 đến 1.0");
                return "redirect:/settings";
            }
        } catch (NumberFormatException ex) {
            redirectAttributes.addFlashAttribute("error", "Temperature không hợp lệ");
            return "redirect:/settings";
        }

        // Prepare a map of settings to update.
        Map<String, String> updateMap = new HashMap<>();
        updateMap.put("BOT_SEND_NOTIFICATION_AFTER_ORDER", (telegramStatus != null && !telegramStatus.isEmpty()) ? "1" : "0");
        updateMap.put("BOT_USERNAME", telegramUsername != null ? telegramUsername : "");
        updateMap.put("BOT_TOKEN", telegramToken != null ? telegramToken : "");
        updateMap.put("BOT_CHAT_ID", telegramChatId != null ? telegramChatId : "");
        updateMap.put("MAINTENANCE", (maintenance != null && !maintenance.isEmpty()) ? "1" : "0");
        updateMap.put("CHATBOT_ENABLE", chatbotEnable != null ? chatbotEnable : "0");
        updateMap.put("LOCAL_CHATBOT_MODEL", localChatbotModel != null ? localChatbotModel : "");
        updateMap.put("LOCAL_CHATBOT_URL", localChatbotUrl != null ? localChatbotUrl : "");
        updateMap.put("LOCAL_CHATBOT_TEMPERATURE", localChatbotTemperature != null ? localChatbotTemperature : "");
        updateMap.put("GEMINI_API_KEY", geminiApiKey != null ? geminiApiKey : "");

        // Update settings via SettingsService
        settingsService.updateSettings(updateMap);

        // Handle Bot Avatar upload
        if (botAvatar != null && !botAvatar.isEmpty()) {
            try {
                String fileName = StringUtils.cleanPath(botAvatar.getOriginalFilename());
                String randomFileName = UUID.randomUUID().toString() + "_" + fileName;
                File uploadDir = new File("img");
                if (!uploadDir.exists()) {
                    uploadDir.mkdirs();
                }
                File dest = new File(uploadDir, randomFileName);
                Files.copy(botAvatar.getInputStream(), dest.toPath(), StandardCopyOption.REPLACE_EXISTING);
                settingsService.updateSetting("CHATBOT_AVATAR", "/img/" + randomFileName);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }

// Handle User Avatar upload
        if (userAvatar != null && !userAvatar.isEmpty()) {
            try {
                String fileName = StringUtils.cleanPath(userAvatar.getOriginalFilename());
                String randomFileName = UUID.randomUUID().toString() + "_" + fileName;
                File uploadDir = new File("img");
                if (!uploadDir.exists()) {
                    uploadDir.mkdirs();
                }
                File dest = new File(uploadDir, randomFileName);
                Files.copy(userAvatar.getInputStream(), dest.toPath(), StandardCopyOption.REPLACE_EXISTING);
                settingsService.updateSetting("CHATBOT_USER_AVATAR", "/img/" + randomFileName);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }

        redirectAttributes.addFlashAttribute("success", "Cài đặt đã được cập nhật!");
        return "redirect:/settings";
    }

    private String getFileExtension(String fileName) {
        if (fileName == null || !fileName.contains(".")) return "";
        return StringUtils.getFilenameExtension(fileName);
    }
}
