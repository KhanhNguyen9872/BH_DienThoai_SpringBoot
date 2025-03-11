package com.springboot.dev_spring_boot_demo.controller;

import com.springboot.dev_spring_boot_demo.dto.ProfileForm;
import com.springboot.dev_spring_boot_demo.entity.Admin;
import com.springboot.dev_spring_boot_demo.service.AdminService;
import com.springboot.dev_spring_boot_demo.service.NotificationService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.DigestUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Controller
public class ProfileController extends HeaderController {

    private final AdminService adminService;

    // Directory to store profile pictures; defaults to "./profile_pictures"
    @Value("${app.admin.profilePictures.dir:profile_pictures}")
    private String profilePicturesDir;

    public ProfileController(AdminService adminService, NotificationService notificationService) {
        super(adminService, notificationService);
        this.adminService = adminService;
    }

    /**
     * Displays the profile settings page.
     */
    @GetMapping("/profile")
    public String profile(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        addHeaderDataToModel(userDetails, model);
        // Retrieve current admin by username
        Admin admin = adminService.findByUsername(userDetails.getUsername());
        ProfileForm profileForm = new ProfileForm();
        profileForm.setName(admin.getFullName());
        profileForm.setEmail(admin.getEmail());
        profileForm.setImg(admin.getImg());
        model.addAttribute("profileForm", profileForm);
        return "profile"; // Corresponds to templates/profile.html
    }

    /**
     * Handles profile updates.
     */
    @PostMapping("/profile")
    public String updateProfile(@Valid @ModelAttribute("profileForm") ProfileForm profileForm,
                                BindingResult result,
                                @RequestParam(name = "profile_picture", required = false) MultipartFile file,
                                RedirectAttributes redirectAttributes,
                                @AuthenticationPrincipal UserDetails userDetails,
                                Model model) {
        if (result.hasErrors()) {
            addHeaderDataToModel(userDetails, model);
            return "profile";
        }

        // Retrieve current admin
        Admin admin = adminService.findByUsername(userDetails.getUsername());
        admin.setFullName(profileForm.getName());
        admin.setEmail(profileForm.getEmail());

        // If a new password is provided, validate the old password and update it.
        if (profileForm.getPassword() != null && !profileForm.getPassword().trim().isEmpty()) {
            if (!md5(profileForm.getOldPassword()).equals(admin.getPassword())) {
                addHeaderDataToModel(userDetails, model);
                result.rejectValue("oldPassword", "error.profileForm", "Mật khẩu hiện tại không chính xác");
                return "profile";
            }
            admin.setPassword(md5(profileForm.getPassword()));
        }

        // If a new profile picture is uploaded, store it and update the image field.
        if (file != null && !file.isEmpty()) {
            try {
                String path = storeFile(file, profilePicturesDir);
                admin.setImg(path);
            } catch (IOException e) {
                addHeaderDataToModel(userDetails, model);
                result.rejectValue("profile_picture", "error.profileForm", "Không thể lưu ảnh: " + e.getMessage());
                return "profile";
            }
        }

        adminService.save(admin);
        redirectAttributes.addFlashAttribute("success", "Thông tin cá nhân đã được cập nhật!");
        return "redirect:/profile";
    }

    /**
     * Helper method to store an uploaded file in the specified directory.
     */
    private String storeFile(MultipartFile file, String directory) throws IOException {
        Path uploadDir = Paths.get(directory);
        if (!Files.exists(uploadDir)) {
            Files.createDirectories(uploadDir);
        }
        String filename = System.currentTimeMillis() + "_" + file.getOriginalFilename();
        Path filePath = uploadDir.resolve(filename);
        Files.copy(file.getInputStream(), filePath);
        // Return the relative path (e.g., "profile_pictures/filename.ext")
        return directory + "/" + filename;
    }

    /**
     * Helper method to compute MD5 hash.
     */
    private String md5(String input) {
        return DigestUtils.md5DigestAsHex(input.getBytes());
    }
}
