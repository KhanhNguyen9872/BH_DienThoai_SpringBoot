package com.springboot.dev_spring_boot_demo.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.springboot.dev_spring_boot_demo.entity.*;
import com.springboot.dev_spring_boot_demo.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

@Controller
public class DashboardController extends HeaderController {
    private final ProductService productService;
    private final OrderService orderService;
    private final UserInfoService userInfoService;
    private final ObjectMapper objectMapper;

    @Autowired
    public DashboardController(AdminService adminService, NotificationService notificationService, ProductService productService, OrderService orderService, UserInfoService userInfoService, ObjectMapper objectMapper) {
        super(adminService, notificationService);
        this.productService = productService;
        this.orderService = orderService;
        this.userInfoService = userInfoService;
        this.objectMapper = objectMapper;
    }

    @GetMapping("/dashboard")
    public String dashboard(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        addHeaderDataToModel(userDetails, model);

        // Retrieve all products using the service
        Page<Product> productsData = productService.findAll(Pageable.unpaged());
        List<Product> products = productsData.getContent();
        long productCount = products.size();
        long totalQuantity = products.stream().mapToLong(product -> {
            String colorJson = product.getColor();
            if (colorJson != null && !colorJson.isEmpty()) {
                try {
                    // Parse the JSON array stored in the 'color' field
                    List<Map<String, Object>> colors = objectMapper.readValue(colorJson, new TypeReference<List<Map<String, Object>>>() {});
                    return colors.stream().mapToLong(color -> {
                        Object qty = color.get("quantity");
                        if (qty instanceof Number) {
                            return ((Number) qty).longValue();
                        } else {
                            try {
                                return Long.parseLong(qty.toString());
                            } catch (Exception e) {
                                return 0L;
                            }
                        }
                    }).sum();
                } catch (Exception e) {
                    // Parsing error, count 0 for this product
                    return 0L;
                }
            }
            return 0L;
        }).sum();

        // Retrieve all orders using the service and calculate total sold quantity from each order's OrderInfo
        Page<Order> ordersData = orderService.findAll(Pageable.unpaged());
        List<Order> orders = ordersData.getContent();
        long totalSoldQuantity = orders.stream().mapToLong(order -> {
            OrderInfo orderInfo = order.getOrderInfo();
            if (orderInfo != null) {
                String productsJson = orderInfo.getProducts();
                if (productsJson != null && !productsJson.isEmpty()) {
                    try {
                        List<Map<String, Object>> orderProducts = objectMapper.readValue(productsJson, new TypeReference<List<Map<String, Object>>>() {});
                        return orderProducts.stream().mapToLong(prod -> {
                            Object qty = prod.get("quantity");
                            if (qty instanceof Number) {
                                return ((Number) qty).longValue();
                            } else {
                                try {
                                    return Long.parseLong(qty.toString());
                                } catch (Exception e) {
                                    return 0L;
                                }
                            }
                        }).sum();
                    } catch (Exception e) {
                        return 0L;
                    }
                }
            }
            return 0L;
        }).sum();

        // Retrieve total users count and total orders count using their services
        long totalUsers = userInfoService.findAll(Pageable.unpaged()).getContent().size();
        long totalOrders = orders.size();

        // Pass values to the view
        model.addAttribute("productCount", productCount);
        model.addAttribute("totalQuantity", totalQuantity);
        model.addAttribute("totalSoldQuantity", totalSoldQuantity);
        model.addAttribute("totalUsers", totalUsers);
        model.addAttribute("totalOrders", totalOrders);

        return "dashboard";
    }
}
