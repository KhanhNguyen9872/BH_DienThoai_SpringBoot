package com.springboot.dev_spring_boot_demo.controller;

import com.springboot.dev_spring_boot_demo.entity.Voucher;
import com.springboot.dev_spring_boot_demo.service.AdminService;
import com.springboot.dev_spring_boot_demo.service.NotificationService;
import com.springboot.dev_spring_boot_demo.service.UserInfoService;
import com.springboot.dev_spring_boot_demo.service.VoucherService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/vouchers")
public class VoucherController extends HeaderController {

    private final VoucherService voucherService;
    private final UserInfoService userInfoService;

    @Autowired
    public VoucherController(AdminService adminService, NotificationService notificationService, VoucherService voucherService, UserInfoService userInfoService) {
        super(adminService, notificationService);
        this.voucherService = voucherService;
        this.userInfoService = userInfoService;
    }

    // List vouchers with pagination
    @GetMapping
    public String listVouchers(@AuthenticationPrincipal UserDetails userDetails,
                               Model model,
                               @RequestParam(defaultValue = "0") int page,
                               @RequestParam(defaultValue = "10") int size) {
        addHeaderDataToModel(userDetails, model);
        Pageable pageable = PageRequest.of(page, size);
        Page<Voucher> vouchersPage = voucherService.findAll(pageable);
        model.addAttribute("vouchers", vouchersPage);
        return "vouchers"; // The Thymeleaf template name for voucher management
    }

    @GetMapping("/create")
    public String showCreateForm(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        addHeaderDataToModel(userDetails, model);
        model.addAttribute("voucher", new Voucher());
        // Retrieve all users (assumes AdminService is available and injected)
        model.addAttribute("users", userInfoService.findAll(Pageable.unpaged()).getContent());
        // You can also initialize the pre-selected lists as empty collections if needed
        model.addAttribute("selectedLimitUsers", new ArrayList<>());
        model.addAttribute("selectedUsers", new ArrayList<>());
        return "vouchers/create";
    }

    @PostMapping
    public String saveVoucher(@Valid @ModelAttribute("voucher") Voucher voucher,
                              @RequestParam(value = "limit_user", required = false) List<String> limitUserList,
                              @RequestParam(value = "user_id", required = false) List<String> userIdList,
                              RedirectAttributes redirectAttributes) {
        try {
            // Convert discount percentage to decimal (e.g., 10 becomes 0.1)
            if (voucher.getDiscount() != null) {
                voucher.setDiscount(voucher.getDiscount().divide(BigDecimal.valueOf(100)));
            }

            // Process "limit_user" values:
            // If no users selected, set to "[]", otherwise join the list with commas.
            if (limitUserList == null || limitUserList.isEmpty()) {
                voucher.setLimitUser("[]");
            } else {
                voucher.setLimitUser("[" + limitUserList.stream().collect(Collectors.joining(",")) + "]");
            }

            // Process "user_id" values:
            if (userIdList == null || userIdList.isEmpty()) {
                voucher.setUserId("[]");
            } else {
                voucher.setUserId("[" + userIdList.stream().collect(Collectors.joining(",")) + "]");
            }

            voucherService.save(voucher);
            redirectAttributes.addFlashAttribute("success", "Voucher đã được tạo thành công!");
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("error", "Có lỗi xảy ra khi tạo voucher.");
        }
        return "redirect:/vouchers";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@AuthenticationPrincipal UserDetails userDetails,
                               @PathVariable("id") Long id,
                               Model model,
                               RedirectAttributes redirectAttributes) {
        addHeaderDataToModel(userDetails, model);
        Voucher voucher = voucherService.findById(id);
        if (voucher == null) {
            redirectAttributes.addFlashAttribute("error", "Không tìm thấy voucher.");
            return "redirect:/vouchers";
        }
        model.addAttribute("voucher", voucher);

        // Retrieve all users (assuming you have a service to get them)
        model.addAttribute("users", userInfoService.findAll(Pageable.unpaged()).getContent());

        // Parse JSON strings into Lists of Strings for pre-selection
        // (Assuming a helper method parseJsonArray exists, or you can do simple splitting)
        model.addAttribute("selectedLimitUsers", voucher.getLimitUser() != null ?
                List.of(voucher.getLimitUser().replaceAll("[\\[\\]\\s]", "").split(",")) : List.of());
        model.addAttribute("selectedUsers", voucher.getUserId() != null ?
                List.of(voucher.getUserId().replaceAll("[\\[\\]\\s]", "").split(",")) : List.of());

        return "vouchers/edit";
    }

    // Update an existing voucher
    @PostMapping("/update")
    public String updateVoucher(@Valid @ModelAttribute("voucher") Voucher voucher,
                                @RequestParam(value = "limit_user", required = false) List<String> limitUserList,
                                @RequestParam(value = "user_id", required = false) List<String> userIdList,
                                RedirectAttributes redirectAttributes) {
        try {
            // Convert discount percentage (e.g., 10 for 10%) to decimal (0.1)
            if (voucher.getDiscount() != null) {
                voucher.setDiscount(voucher.getDiscount().divide(BigDecimal.valueOf(100)));
            }

            // Process multi-select for "Giới Hạn Người Dùng"
            if (limitUserList == null || limitUserList.isEmpty()) {
                voucher.setLimitUser("[]");
            } else {
                voucher.setLimitUser("[" + limitUserList.stream().collect(Collectors.joining(",")) + "]");
            }

            // Process multi-select for "Người Dùng Đã Nhập"
            if (userIdList == null || userIdList.isEmpty()) {
                voucher.setUserId("[]");
            } else {
                voucher.setUserId("[" + userIdList.stream().collect(Collectors.joining(",")) + "]");
            }

            voucherService.save(voucher);
            redirectAttributes.addFlashAttribute("success", "Voucher đã được cập nhật thành công!");
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("error", "Có lỗi xảy ra khi cập nhật voucher.");
        }
        return "redirect:/vouchers";
    }

    // Delete a voucher
    @PostMapping("/delete/{id}")
    public String deleteVoucher(@PathVariable("id") Long id,
                                RedirectAttributes redirectAttributes) {
        try {
            voucherService.deleteById(id);
            redirectAttributes.addFlashAttribute("success", "Voucher đã được xóa thành công!");
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("error", "Có lỗi xảy ra khi xóa voucher.");
        }
        return "redirect:/vouchers";
    }
}
