package com.springboot.dev_spring_boot_demo.controller;

import com.springboot.dev_spring_boot_demo.entity.*;
import com.springboot.dev_spring_boot_demo.service.AdminService;
import com.springboot.dev_spring_boot_demo.service.NotificationService;
import com.springboot.dev_spring_boot_demo.service.OrderService;
import com.springboot.dev_spring_boot_demo.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Controller
public class OrderController extends HeaderController {

    private final OrderService orderService;
    private final ProductService productService;

    @Autowired
    public OrderController(AdminService adminService, NotificationService notificationService, OrderService orderService, ProductService productService) {
        super(adminService, notificationService);
        this.orderService = orderService;
        this.productService = productService;
    }

    @GetMapping("/orders/delete/{id}")
    public String deleteProduct(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        orderService.deleteById(id);
        String resultMsg = URLEncoder.encode("Đơn hàng đã được xóa thành công!", StandardCharsets.UTF_8);
        redirectAttributes.addFlashAttribute("success", "Đơn hàng đã được xóa thành công!");
        return "redirect:/orders?success=" + resultMsg;
    }

    @GetMapping("/orders")
    public String listOrders(@AuthenticationPrincipal UserDetails userDetails,
                             Model model,
                             @RequestParam(defaultValue = "0") int page,
                             @RequestParam(defaultValue = "10") int size) {
        addHeaderDataToModel(userDetails, model);

        // Retrieve orders using pageable
        Pageable pageable = PageRequest.of(page, size);
        org.springframework.data.domain.Page<Order> ordersPage = orderService.findAll(pageable);
        List<Order> orders = ordersPage.getContent();

        // Update payment method display for each order
        for (Order order : orders) {
            OrderInfo orderInfo = order.getOrderInfo();
            String payment = orderInfo.getPayment();
            if ("nganhang".equalsIgnoreCase(payment)) {
                orderInfo.setPayment("Ngân hàng");
            } else if ("momo".equalsIgnoreCase(payment)) {
                orderInfo.setPayment("MoMo");
            } else if ("tienmat".equalsIgnoreCase(payment)) {
                orderInfo.setPayment("Tiền mặt");
            }
        }

        model.addAttribute("orders", orders);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", ordersPage.getTotalPages());
        return "orders"; // refers to orders.html in your templates folder
    }


    @GetMapping("/orders/{id}")
    public String showOrder(@AuthenticationPrincipal UserDetails userDetails, @PathVariable("id") Long id, Model model) {
        addHeaderDataToModel(userDetails, model);
        Order order = orderService.findById(id);
        if (order == null) {
            String errorMsg = URLEncoder.encode("Đơn hàng không tồn tại", StandardCharsets.UTF_8);
            return "redirect:/orders?error=" + errorMsg;
        }
        model.addAttribute("order", order);
        return "orders/show"; // resolves to templates/orders/show.html
    }

    // Confirm an order: update status from "Đang chờ xác nhận" to "Đang chờ giao hàng"
    @PostMapping("/orders/confirm/{id}")
    public String confirmOrder(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        Order order = orderService.findById(id);
        if (order != null && order.getOrderInfo() != null
                && "Đang chờ xác nhận".equals(order.getOrderInfo().getStatus())) {
            order.getOrderInfo().setStatus("Đang chờ giao hàng");
            orderService.save(order); // updates the order in the database
            redirectAttributes.addFlashAttribute("success", "Đơn hàng đã được xác nhận!");
        } else {
            redirectAttributes.addFlashAttribute("error", "Đơn hàng không tồn tại hoặc đã xác nhận từ trước!");
        }
        return "redirect:/orders/" + id;
    }

    // Cancel an order: update status and update product quantities
    @PostMapping("/orders/cancel/{id}")
    public String cancelOrder(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        Order order = orderService.findById(id);
        if (order == null || order.getOrderInfo() == null) {
            redirectAttributes.addFlashAttribute("error", "Đơn hàng không tồn tại!");
            return "redirect:/orders";
        }

        String status = order.getOrderInfo().getStatus();
        // Do not allow cancellation if order is "Đang giao hàng" or "Đã giao hàng"
        if ("Đang giao hàng".equals(status) || "Đã giao hàng".equals(status)) {
            redirectAttributes.addFlashAttribute("error", "Không thể hủy đơn hàng khi đang giao hàng hoặc đã giao hàng.");
            return "redirect:/orders";
        }

        // Set order status to "Đã hủy" and update the order
        order.getOrderInfo().setStatus("Đã hủy");
        orderService.save(order);

        // For each product in the order, update the available quantity for the corresponding color variant.
        // It is assumed that order.getOrderInfo().getOrderInfoProducts() returns a list of OrderInfoProducts,
        // and that each OrderInfoProducts contains an id, color, and quantity.
        if (order.getOrderInfo().getProducts() != null) {
            for (OrderInfoProducts productDetail : order.getOrderInfo().getOrderInfoProducts()) {
                // Retrieve the product from the database by its id
                Product product = productService.findById(productDetail.getId());
                if (product != null) {
                    // Assuming product.toProductColorsObject() returns a List<ProductColor> where each ProductColor has a name and quantity.
                    List<ProductColor> colors = product.toProductColorsObject();
                    if (colors != null) {
                        for (ProductColor color : colors) {
                            // Increase the quantity if the color matches the one from the order detail
                            if (color.getName().equals(productDetail.getColor())) {
                                color.setQuantity(color.getQuantity() + productDetail.getQuantity());
                            }
                        }
                        productService.save(product);
                    }
                }
            }
        }

        redirectAttributes.addFlashAttribute("success", "Đơn hàng đã được hủy và số lượng sản phẩm đã được cập nhật.");
        return "redirect:/orders";
    }
}
