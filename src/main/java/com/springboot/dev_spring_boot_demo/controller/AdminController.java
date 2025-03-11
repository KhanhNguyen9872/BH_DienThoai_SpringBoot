package com.springboot.dev_spring_boot_demo.controller;

import com.springboot.dev_spring_boot_demo.entity.Admin;
import com.springboot.dev_spring_boot_demo.service.AdminService;
import com.springboot.dev_spring_boot_demo.service.NotificationService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
@RequestMapping("/admins")
public class AdminController extends HeaderController {

    private final AdminService adminService;

    // Directory to store uploaded admin images.
    @Value("${app.admin.profilePictures.dir:profile_pictures}")
    private String profilePicturesDir;


    public AdminController(AdminService adminService, NotificationService notificationService) {
        super(adminService, notificationService);
        this.adminService = adminService;
    }

    /**
     * Display a paginated list of admin accounts.
     */
    @GetMapping
    public String index(Pageable pageable,
                        Model model,
                        @AuthenticationPrincipal UserDetails userDetails) {
        addHeaderDataToModel(userDetails, model);
        Page<Admin> adminsPage = adminService.findAll(pageable);
        model.addAttribute("adminsPage", adminsPage);
        return "admins"; // Thymeleaf template for listing admin accounts
    }

    /**
     * Show the form for creating a new admin account.
     */
    @GetMapping("/create")
    public String create(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        addHeaderDataToModel(userDetails, model);
        model.addAttribute("admin", new Admin());
        return "admins/create"; // Path to the Thymeleaf template
    }

    @PostMapping
    public String store(@AuthenticationPrincipal UserDetails userDetails,
                        @Valid @ModelAttribute("admin") Admin admin,
                        BindingResult result,
                        @RequestParam(name = "imgFile", required = false) MultipartFile file,
                        RedirectAttributes redirectAttributes,
                        Model model) {
        if (result.hasErrors()) {
            addHeaderDataToModel(userDetails, model);
            model.addAttribute("admin", admin);
            return "admins/create";
        }

        // Handle image upload if provided.
        if (file != null && !file.isEmpty()) {
            try {
                String filePath = storeFile(file, profilePicturesDir);
                admin.setImg(filePath);
            } catch (IOException e) {
                addHeaderDataToModel(userDetails, model);
                result.rejectValue("img", "error.admin", "Không thể lưu ảnh: " + e.getMessage());
                model.addAttribute("admin", admin);
                return "admins/create";
            }
        }

        // Hash the password using MD5 as requested.
        admin.setPassword(md5(admin.getPassword()));

        adminService.save(admin);
        redirectAttributes.addFlashAttribute("success", "Tài khoản admin đã được tạo thành công.");
        return "redirect:/admins";
    }

    // Show the edit form
    @GetMapping("/edit/{id}")
    public String edit(@PathVariable("id") Long id,
                       Model model,
                       @AuthenticationPrincipal UserDetails userDetails) {
        addHeaderDataToModel(userDetails, model);
        Admin admin = adminService.findById(id);
        if (admin == null) {
            // Optionally redirect with error if admin not found
            return "redirect:/admins";
        }
        model.addAttribute("admin", admin);
        return "admins/edit"; // Thymeleaf template path
    }

    // Handle the update submission
    @PostMapping("/update/{id}")
    public String update(@PathVariable("id") Long id,
                         @Valid @ModelAttribute("admin") Admin admin,
                         BindingResult result,
                         @RequestParam(name = "imgFile", required = false) MultipartFile file,
                         RedirectAttributes redirectAttributes,
                         Model model,
                         @AuthenticationPrincipal UserDetails userDetails) {
        addHeaderDataToModel(userDetails, model);
        if (result.hasErrors()) {
            model.addAttribute("admin", admin);
            return "admins/edit";
        }

        // Retrieve the existing admin record.
        Admin existingAdmin = adminService.findById(id);
        if (existingAdmin == null) {
            redirectAttributes.addFlashAttribute("error", "Không tìm thấy tài khoản admin.");
            return "redirect:/admins";
        }

        // Update fields.
        existingAdmin.setFullName(admin.getFullName());
        existingAdmin.setEmail(admin.getEmail());
        existingAdmin.setUsername(admin.getUsername());

        // Handle image upload if a new file is provided.
        if (file != null && !file.isEmpty()) {
            try {
                // Save image to profile_pictures folder.
                String filePath = storeFile(file, profilePicturesDir);
                existingAdmin.setImg(filePath);
            } catch (IOException e) {
                result.rejectValue("img", "error.admin", "Không thể lưu ảnh: " + e.getMessage());
                model.addAttribute("admin", admin);
                return "admins/edit";
            }
        }

        // Optionally, update the password if you want to allow password change.
        // For now, if the password is empty in the form, we leave it unchanged.
        if (admin.getPassword() != null && !admin.getPassword().trim().isEmpty()) {
            existingAdmin.setPassword(md5(admin.getPassword()));
        }

        adminService.save(existingAdmin);
        redirectAttributes.addFlashAttribute("success", "Tài khoản admin đã được cập nhật thành công.");
        return "redirect:/admins";
    }

    /**
     * Delete the specified admin account.
     * Prevents deletion of the currently authenticated admin.
     */
    @PostMapping("/delete/{id}")
    public String delete(@PathVariable("id") Long id,
                         RedirectAttributes redirectAttributes,
                         @AuthenticationPrincipal UserDetails userDetails) {
        Admin adminToDelete = adminService.findById(id);
        if (adminToDelete == null) {
            redirectAttributes.addFlashAttribute("error", "Tài khoản admin không tồn tại.");
            return "redirect:/admins";
        }

        Admin currentAdmin = adminService.findByUsername(userDetails.getUsername());
        if (adminToDelete.getId().equals(currentAdmin.getId())) {
            redirectAttributes.addFlashAttribute("error", "Không thể xóa tài khoản hiện tại.");
            return "redirect:/admins";
        }

        adminService.deleteById(id);
        redirectAttributes.addFlashAttribute("success", "Tài khoản admin đã được xóa thành công.");
        return "redirect:/admins";
    }

    /**
     * Helper method to store an uploaded file in the specified directory.
     * Returns the relative file path for later retrieval.
     */
    private String storeFile(MultipartFile file, String directory) throws IOException {
        // Ensure the directory exists.
        Path uploadDir = Paths.get(directory);
        if (!Files.exists(uploadDir)) {
            Files.createDirectories(uploadDir);
        }

        // Generate a unique filename. (You can use a more robust approach.)
        String filename = System.currentTimeMillis() + "_" + file.getOriginalFilename();
        Path filePath = uploadDir.resolve(filename);
        Files.copy(file.getInputStream(), filePath);
        // Return the relative path so that it can be used in the view.
        return directory + "/" + filename;
    }

    /**
     * Helper method to compute the MD5 hash of a given input string.
     */
    private String md5(String input) {
        // Spring's DigestUtils provides an MD5 hash implementation.
        return DigestUtils.md5DigestAsHex(input.getBytes());
    }
}
