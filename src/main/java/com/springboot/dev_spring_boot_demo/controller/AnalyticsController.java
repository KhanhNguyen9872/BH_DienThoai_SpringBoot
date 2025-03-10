package com.springboot.dev_spring_boot_demo.controller;

import com.springboot.dev_spring_boot_demo.entity.Order;
import com.springboot.dev_spring_boot_demo.entity.OrderInfo;
import com.springboot.dev_spring_boot_demo.entity.UserInfo;
import com.springboot.dev_spring_boot_demo.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.math.BigDecimal;
import java.text.DateFormatSymbols;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Controller
public class AnalyticsController extends HeaderController {

    private final OrderService orderService;
    private final OrderInfoService orderInfoService;
    private final UserInfoService userInfoService;

    @Autowired
    public AnalyticsController(AdminService adminService, NotificationService notificationService, OrderService orderService,
                               OrderInfoService orderInfoService,
                               UserInfoService userInfoService) {
        super(adminService, notificationService);
        this.orderService = orderService;
        this.orderInfoService = orderInfoService;
        this.userInfoService = userInfoService;
    }

    @GetMapping("/analytics")
    public String index(@AuthenticationPrincipal UserDetails userDetails,
                        Model model) {
        addHeaderDataToModel(userDetails, model);
        // 1) Basic Metrics
        long tongDonHang = orderService.findAll(Pageable.unpaged()).getTotalElements();
        long tongNguoiDung = userInfoService.findAll(Pageable.unpaged()).getTotalElements();

        // Sum totalPrice for orders with status "Đã giao"
        BigDecimal tongDoanhThu = orderInfoService.findAll().stream()
                .filter(oi -> "Đã giao".equalsIgnoreCase(oi.getStatus()))
                .map(oi -> new BigDecimal(oi.getTotalPrice()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        double tyLeChuyenDoi = 4.5;

        // 2) Monthly Orders (orders with status "Đã giao")
        List<OrderInfo> allOrderInfos = orderInfoService.findAll();
        Map<Integer, Long> ordersByMonth = allOrderInfos.stream()
                .filter(oi -> "Đã giao".equalsIgnoreCase(oi.getStatus()) && oi.getOrderAt() != null)
                .collect(Collectors.groupingBy(oi -> oi.getOrderAt().getMonthValue(), Collectors.counting()));

        List<Integer> sortedOrderMonths = new ArrayList<>(ordersByMonth.keySet());
        Collections.sort(sortedOrderMonths);
        List<String> nhanDonHang = new ArrayList<>();
        List<Long> duLieuDonHang = new ArrayList<>();
        for (Integer month : sortedOrderMonths) {
            String label = new DateFormatSymbols(new Locale("vi")).getShortMonths()[month - 1];
            nhanDonHang.add(label);
            duLieuDonHang.add(ordersByMonth.get(month));
        }

        // 3) Monthly New Users
        List<UserInfo> allUsers = userInfoService.findAll(Pageable.unpaged()).getContent();
        Map<Integer, Long> usersByMonth = allUsers.stream()
                .filter(user -> user.getCreatedAt() != null)
                .collect(Collectors.groupingBy(user -> user.getCreatedAt().getMonth(), Collectors.counting()));

        List<Integer> sortedUserMonths = new ArrayList<>(usersByMonth.keySet());
        Collections.sort(sortedUserMonths);
        List<String> nhanNguoiDung = new ArrayList<>();
        List<Long> duLieuNguoiDung = new ArrayList<>();
        for (Integer month : sortedUserMonths) {
            String label = new DateFormatSymbols(new Locale("vi")).getShortMonths()[month - 1];
            nhanNguoiDung.add(label);
            duLieuNguoiDung.add(usersByMonth.get(month));
        }

        // 4) Recent Orders: Get all orders, sort by createdAt descending, and take the top 5.
        List<Order> allOrders = orderService.findAll(Pageable.unpaged()).getContent();
        List<Order> donHangGanDay = allOrders.stream()
                .filter(o -> o.getCreatedAt() != null)
                .sorted(Comparator.comparing(Order::getCreatedAt, Comparator.reverseOrder()))
                .limit(5)
                .collect(Collectors.toList());

        // Add attributes to model
        String formattedRevenue = String.format("%,d", tongDoanhThu.longValue());
        model.addAttribute("formattedRevenue", formattedRevenue);

        model.addAttribute("tongDonHang", tongDonHang);
        model.addAttribute("tongNguoiDung", tongNguoiDung);
        model.addAttribute("tongDoanhThu", tongDoanhThu);
        model.addAttribute("tyLeChuyenDoi", tyLeChuyenDoi);
        model.addAttribute("nhanDonHang", nhanDonHang);
        model.addAttribute("duLieuDonHang", duLieuDonHang);
        model.addAttribute("nhanNguoiDung", nhanNguoiDung);
        model.addAttribute("duLieuNguoiDung", duLieuNguoiDung);
        model.addAttribute("donHangGanDay", donHangGanDay);

        return "analytics";
    }
}
