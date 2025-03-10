package com.springboot.dev_spring_boot_demo.controller;

import com.springboot.dev_spring_boot_demo.entity.Account;
import com.springboot.dev_spring_boot_demo.service.AccountService;
import com.springboot.dev_spring_boot_demo.service.AdminService;
import com.springboot.dev_spring_boot_demo.service.NotificationService;
import com.springboot.dev_spring_boot_demo.service.UserInfoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@Controller
@RequestMapping("/accounts")
public class AccountController extends HeaderController {

    private final AccountService accountService;
    private final UserInfoService userInfoService;

    @Autowired
    public AccountController(AdminService adminService, NotificationService notificationService, AccountService accountService, UserInfoService userInfoService) {
        super(adminService, notificationService);
        this.accountService = accountService;
        this.userInfoService = userInfoService;
    }

    // List accounts with pagination
    @GetMapping
    public String listAccounts(@AuthenticationPrincipal UserDetails userDetails,
                               Model model,
                               @RequestParam(defaultValue = "0") int page,
                               @RequestParam(defaultValue = "10") int size) {
        addHeaderDataToModel(userDetails, model);
        Pageable pageable = PageRequest.of(page, size);
        Page<Account> accountsPage = accountService.findAll(pageable);
        model.addAttribute("accountsPage", accountsPage);
        return "accounts"; // Thymeleaf template for account management
    }

    // Show the form for creating a new account
    @GetMapping("/create")
    public String showCreateForm(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        addHeaderDataToModel(userDetails, model);
        model.addAttribute("account", new Account());
        model.addAttribute("users", userInfoService.findAll(Pageable.unpaged()).getContent());
        return "accounts/create"; // Thymeleaf template for account creation/edit
    }

    @PostMapping
    public String saveAccount(@Valid @ModelAttribute("account") Account account,
                              RedirectAttributes redirectAttributes) {
        try {
            // Hash password with MD5 before saving
            account.setPassword(hashMD5(account.getPassword()));

            accountService.save(account);
            redirectAttributes.addFlashAttribute("success", "Tài khoản đã được tạo thành công!");
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("error", "Có lỗi xảy ra khi tạo tài khoản.");
        }
        return "redirect:/accounts";
    }

    // MD5 Hashing Method
    private String hashMD5(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] messageDigest = md.digest(input.getBytes());
            BigInteger number = new BigInteger(1, messageDigest);
            String hashText = number.toString(16);
            while (hashText.length() < 32) {
                hashText = "0" + hashText;
            }
            return hashText;
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("MD5 hashing failed", e);
        }
    }

    // Show the form for editing an existing account
    @GetMapping("/edit/{id}")
    public String showEditForm(@AuthenticationPrincipal UserDetails userDetails,
                               @PathVariable("id") Long id,
                               Model model,
                               RedirectAttributes redirectAttributes) {
        addHeaderDataToModel(userDetails, model);
        Account account = accountService.findById(id);
        if (account == null) {
            redirectAttributes.addFlashAttribute("error", "Không tìm thấy tài khoản.");
            return "redirect:/accounts";
        }
        model.addAttribute("account", account);
        model.addAttribute("users", userInfoService.findAll(Pageable.unpaged()).getContent());
        return "accounts/edit"; // Thymeleaf template for editing an account
    }


    @PostMapping("/update")
    public String updateAccount(@Valid @ModelAttribute("account") Account account,
                                RedirectAttributes redirectAttributes) {
        try {
            // Retrieve the existing account from the database
            Account existingAccount = accountService.findById(account.getId());
            if (existingAccount == null) {
                redirectAttributes.addFlashAttribute("error", "Không tìm thấy tài khoản.");
                return "redirect:/accounts";
            }

            // Update fields
            existingAccount.setUsername(account.getUsername());
            existingAccount.setLockAcc(account.getLockAcc());
            existingAccount.setUser(account.getUser());

            // Update password only if a new password is provided
            if (account.getPassword() != null && !account.getPassword().trim().isEmpty()) {
                existingAccount.setPassword(hashMD5(account.getPassword()));
            }

            accountService.save(existingAccount);
            redirectAttributes.addFlashAttribute("success", "Tài khoản đã được cập nhật thành công!");
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("error", "Có lỗi xảy ra khi cập nhật tài khoản.");
        }
        return "redirect:/accounts";
    }

    // Delete an account
    @PostMapping("/delete/{id}")
    public String deleteAccount(@PathVariable("id") Long id,
                                RedirectAttributes redirectAttributes) {
        try {
            accountService.deleteById(id);
            redirectAttributes.addFlashAttribute("success", "Tài khoản đã được xóa thành công!");
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("error", "Có lỗi xảy ra khi xóa tài khoản.");
        }
        return "redirect:/accounts";
    }
}
