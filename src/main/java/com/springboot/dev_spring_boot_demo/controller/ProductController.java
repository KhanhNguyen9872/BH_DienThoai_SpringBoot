package com.springboot.dev_spring_boot_demo.controller;

import com.springboot.dev_spring_boot_demo.entity.Product;
import com.springboot.dev_spring_boot_demo.service.AdminService;
import com.springboot.dev_spring_boot_demo.service.NotificationService;
import com.springboot.dev_spring_boot_demo.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class ProductController extends HeaderController {

    private final ProductService productService;

    @Autowired
    public ProductController(AdminService adminService, NotificationService notificationService, ProductService productService) {
        super(adminService, notificationService);
        this.productService = productService;
    }

    @GetMapping("/products")
    public String listProducts(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        addHeaderDataToModel(userDetails, model);

        List<Product> products = productService.findAll();
        model.addAttribute("products", products);
        return "products"; // refers to products.html in your templates folder
    }
}
