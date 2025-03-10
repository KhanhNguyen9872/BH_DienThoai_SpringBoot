package com.springboot.dev_spring_boot_demo.controller;

import com.springboot.dev_spring_boot_demo.entity.Address;
import com.springboot.dev_spring_boot_demo.entity.UserInfo;
import com.springboot.dev_spring_boot_demo.service.AddressService;
import com.springboot.dev_spring_boot_demo.service.AdminService;
import com.springboot.dev_spring_boot_demo.service.NotificationService;
import com.springboot.dev_spring_boot_demo.service.UserInfoService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class UserController extends HeaderController {

    private final UserInfoService userInfoService;
    private final AddressService addressService;

    @Autowired
    public UserController(AdminService adminService, NotificationService notificationService, UserInfoService userInfoService, AddressService addressService) {
        super(adminService, notificationService);
        this.userInfoService = userInfoService;
        this.addressService = addressService;
    }

    @GetMapping("/users")
    public String listUsers(@AuthenticationPrincipal UserDetails userDetails,
                            Model model,
                            @RequestParam(defaultValue = "0") int page,
                            @RequestParam(defaultValue = "10") int size) {
        addHeaderDataToModel(userDetails, model);

        Pageable pageable = PageRequest.of(page, size);
        Page<UserInfo> usersPage = userInfoService.findAll(pageable);

        model.addAttribute("usersPage", usersPage);
        return "users"; // Refers to your Thymeleaf template for user management
    }

    @GetMapping("/users/create")
    public String showCreateUserForm(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        addHeaderDataToModel(userDetails, model);
        // Add an empty UserInfo object to the model so the form can bind to it.
        model.addAttribute("userInfo", new UserInfo());
        return "users/create"; // resolves to src/main/resources/templates/users/create.html
    }

    @PostMapping("/users")
    public String storeUser(@ModelAttribute("userInfo") @Valid UserInfo userInfo,
                            BindingResult bindingResult,
                            HttpServletRequest request,
                            RedirectAttributes redirectAttributes,
                            @AuthenticationPrincipal UserDetails userDetails,
                            Model model) {
        // Add header data if needed
        addHeaderDataToModel(userDetails, model);

        // If there are validation errors, return to the create form.
        if (bindingResult.hasErrors()) {
            return "users/create";
        }

        // Save the user first. This returns a user with an assigned ID.
        UserInfo savedUser = userInfoService.save(userInfo);

        // Retrieve the dynamic addresses from the request.
        // The addresses are expected to be submitted with names like:
        // addresses[][name], addresses[][address], addresses[][phone]
        String[] names = request.getParameterValues("addresses[][name]");
        String[] addrs = request.getParameterValues("addresses[][address]");
        String[] phones = request.getParameterValues("addresses[][phone]");

        if (names != null && addrs != null && phones != null) {
            for (int i = 0; i < names.length; i++) {
                Address address = new Address();
                address.setUser(savedUser);
                address.setFullName(names[i]);
                address.setAddress(addrs[i]);
                address.setPhone(phones[i]);
                addressService.save(address);
            }
        }

        redirectAttributes.addFlashAttribute("success", "Người dùng mới đã được tạo thành công.");
        return "redirect:/users";
    }

    @GetMapping("/users/edit/{id}")
    public String showEditForm(@PathVariable("id") Long id,
                               @AuthenticationPrincipal UserDetails userDetails,
                               Model model,
                               RedirectAttributes redirectAttributes) {
        addHeaderDataToModel(userDetails, model);

        UserInfo userInfo = userInfoService.findById(id);
        if (userInfo == null) {
            redirectAttributes.addFlashAttribute("error", "Người dùng không tồn tại.");
            return "redirect:/users";
        }

        // Retrieve addresses from the user. This uses the one-to-many mapping.
        // If the addresses are lazily loaded, accessing them here will initialize the collection.
        List<Address> addresses = userInfo.getAddresses();
        model.addAttribute("userInfo", userInfo);
        model.addAttribute("addresses", addresses);

        return "users/edit"; // resolves to templates/users/edit.html
    }

    @PostMapping("/users/{id}")
    public String updateUser(@PathVariable("id") Long id,
                             @ModelAttribute("userInfo") @Valid UserInfo userInfo,
                             BindingResult bindingResult,
                             HttpServletRequest request,
                             RedirectAttributes redirectAttributes,
                             @AuthenticationPrincipal UserDetails userDetails,
                             Model model) {
        addHeaderDataToModel(userDetails, model);

        if (bindingResult.hasErrors()) {
            // Repopulate addresses from the existing user for the view.
//            UserInfo existingUser = userInfoService.findById(id);
//            model.addAttribute("userInfo", userInfo);
//            model.addAttribute("addresses", existingUser.getAddresses());
            return "users/edit";
        }

        // Load the existing user from the database.
        UserInfo existingUser = userInfoService.findById(id);
        if (existingUser == null) {
            redirectAttributes.addFlashAttribute("error", "Người dùng không tồn tại.");
            return "redirect:/users";
        }

        // Update basic fields.
        existingUser.setFirstName(userInfo.getFirstName());
        existingUser.setLastName(userInfo.getLastName());
        existingUser.setEmail(userInfo.getEmail());

        // Process dynamic addresses from the form.
        // We expect parameters with names: newAddresses[][name], newAddresses[][address], newAddresses[][phone]
        String[] names = request.getParameterValues("newAddresses[][name]");
        String[] addrs = request.getParameterValues("newAddresses[][address]");
        String[] phones = request.getParameterValues("newAddresses[][phone]");

        // Clear existing addresses.
        if (existingUser.getAddresses() != null) {
            existingUser.getAddresses().clear();
        }

        if (names != null && addrs != null && phones != null) {
            for (int i = 0; i < names.length; i++) {
                Address address = new Address();
                address.setFullName(names[i]);
                address.setAddress(addrs[i]);
                address.setPhone(phones[i]);
                address.setUser(existingUser);
                existingUser.getAddresses().add(address);
            }
        }

        // Save the updated user. Cascade settings will persist the new addresses.
        userInfoService.save(existingUser);

        redirectAttributes.addFlashAttribute("success", "Thông tin người dùng đã được cập nhật thành công.");
        return "redirect:/users";
    }
}
