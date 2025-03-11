package com.springboot.dev_spring_boot_demo.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.springboot.dev_spring_boot_demo.dto.ProductColorForm;
import com.springboot.dev_spring_boot_demo.dto.ProductForm;
import com.springboot.dev_spring_boot_demo.entity.Product;
import com.springboot.dev_spring_boot_demo.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Controller
public class ProductController extends HeaderController {

    private final ProductService productService;
    private final UserInfoService userService; // To load users for the "favorite" field
    private final PColorService pColorService;

    @Autowired
    public ProductController(AdminService adminService, NotificationService notificationService, ProductService productService, UserInfoService userService, PColorService pColorService) {
        super(adminService, notificationService);
        this.productService = productService;
        this.userService = userService;
        this.pColorService = pColorService;
    }

    @GetMapping("/products")
    public String listProducts(@AuthenticationPrincipal UserDetails userDetails,
                               Model model,
                               @RequestParam(defaultValue = "0") int page,
                               @RequestParam(defaultValue = "10") int size) {
        addHeaderDataToModel(userDetails, model);

        Pageable pageable = PageRequest.of(page, size);
        Page<Product> productsPage = productService.findAll(pageable);

        model.addAttribute("products", productsPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", productsPage.getTotalPages());

        return "products"; // refers to products.html in your templates folder
    }

    @GetMapping("/products/delete/{id}")
    public String deleteProduct(@PathVariable("id") int id, RedirectAttributes redirectAttributes) throws UnsupportedEncodingException {
        productService.deleteById(id);
        redirectAttributes.addFlashAttribute("success", "Sản phẩm đã được xóa thành công!");
        String resultMsg = URLEncoder.encode("Sản phẩm đã được xóa thành công", "UTF-8");
        return "redirect:/products?success=" + resultMsg;
    }

    // Show the create product page
    @GetMapping("/products/create")
    public String showCreateForm(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        addHeaderDataToModel(userDetails, model);
        model.addAttribute("productForm", new ProductForm());
        model.addAttribute("users", userService.findAll(Pageable.unpaged()).getContent());
        // Load all available PColor objects
        model.addAttribute("pcolors", pColorService.findAll());
        return "products/create";
    }

    // Process form submission to create a product
    @PostMapping("/products/store")
    public String storeProduct(@AuthenticationPrincipal UserDetails userDetails, @ModelAttribute("productForm") ProductForm form,
                               BindingResult result, Model model) throws UnsupportedEncodingException {
        if (result.hasErrors()) {
            addHeaderDataToModel(userDetails, model);
            model.addAttribute("users", userService.findAll(Pageable.unpaged()).getContent());
            return "products/create";
        }

        Product product = new Product();
        product.setName(form.getName());
        product.setDescription(form.getDescription());
        product.setCreatedAt(LocalDateTime.now());
        product.setUpdatedAt(LocalDateTime.now());

        // Convert favorites list to JSON string
        try {
            ObjectMapper mapper = new ObjectMapper();
            String favoriteJson = mapper.writeValueAsString(form.getFavorite());
            product.setFavorite(favoriteJson);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Process the colors: handle image file uploads and then convert to JSON string
        // Process the colors: handle image file uploads and then convert to JSON string
        List<Object> colorsToStore = new ArrayList<>();
        if (form.getColors() != null) {
            for (ProductColorForm colorForm : form.getColors()) {
                MultipartFile file = colorForm.getImg();
                String imagePath = "";
                try {
                    if (file != null && !file.isEmpty()) {
                        // Generate a random file name using UUID
                        String fileName = StringUtils.cleanPath(file.getOriginalFilename());
                        String randomFileName = UUID.randomUUID().toString() + "_" + fileName;
                        // Save to the desired directory
                        File uploadDir = new File("img");
                        if (!uploadDir.exists()) {
                            uploadDir.mkdirs();
                        }
                        File dest = new File(uploadDir, randomFileName);
                        Files.copy(file.getInputStream(), dest.toPath(), StandardCopyOption.REPLACE_EXISTING);
                        imagePath = "/img/" + randomFileName;
                    } else {
                        // Sử dụng ảnh mặc định nếu không có ảnh được tải lên
                        imagePath = "/images/default-phone.png";
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                java.util.Map<String, Object> colorMap = new java.util.HashMap<>();
                colorMap.put("name", colorForm.getName());
                colorMap.put("money", colorForm.getMoney());
                colorMap.put("quantity", colorForm.getQuantity());
                if (colorForm.getMoneyDiscount() != null && !colorForm.getMoneyDiscount().equals("0")) {
                    colorMap.put("moneyDiscount", colorForm.getMoneyDiscount());
                }
                colorMap.put("img", imagePath);
                colorsToStore.add(colorMap);
            }
        }

        // Convert colors list to JSON string
        try {
            ObjectMapper mapper = new ObjectMapper();
            String colorsJson = mapper.writeValueAsString(colorsToStore);
            product.setColor(colorsJson);
        } catch (Exception e) {
            e.printStackTrace();
        }

        productService.save(product);
        String resultMsg = URLEncoder.encode("Sản phẩm đã được tạo thành công", "UTF-8");
        return "redirect:/products?success=" + resultMsg;
    }

    @GetMapping("/products/edit/{id}")
    public String showEditForm(@AuthenticationPrincipal UserDetails userDetails, @PathVariable("id") int id, Model model) throws UnsupportedEncodingException {
        addHeaderDataToModel(userDetails, model);

        // Lấy sản phẩm theo id
        Product product = productService.findById(id);
        if (product == null) {
            String errorMsg = URLEncoder.encode("Sản phẩm không tồn tại", "UTF-8");
            return "redirect:/products?error=" + errorMsg;
        }

        // Tạo ProductForm và điền dữ liệu từ sản phẩm
        ProductForm form = new ProductForm();
        form.setId(product.getId());
        form.setName(product.getName());
        form.setDescription(product.getDescription());

        // Chuyển đổi trường favorite (JSON) thành List<Integer>
        try {
            ObjectMapper mapper = new ObjectMapper();
            List<Integer> favorites = mapper.readValue(product.getFavorite(), new TypeReference<List<Integer>>() {});
            form.setFavorite(favorites);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Chuyển đổi trường color (JSON) thành List<ProductColorForm>
        try {
            ObjectMapper mapper = new ObjectMapper();
            List<ProductColorForm> colors = mapper.readValue(product.getColor(), new TypeReference<List<ProductColorForm>>() {});
            form.setColors(colors);
        } catch (Exception e) {
            // Nếu không có dữ liệu màu, set danh sách rỗng
            form.setColors(new ArrayList<>());
            e.printStackTrace();
        }

        // Nếu danh sách màu vẫn null thì set thành danh sách rỗng
        if(form.getColors() == null) {
            form.setColors(new ArrayList<>());
        }

        model.addAttribute("productForm", form);
        model.addAttribute("users", userService.findAll(Pageable.unpaged()).getContent());
        model.addAttribute("pcolors", pColorService.findAll());

        return "products/edit";  // Trả về view "products/edit.html"
    }


    @PostMapping("/products/update")
    public String updateProduct(@ModelAttribute("productForm") ProductForm form,
                                BindingResult result, Model model) throws UnsupportedEncodingException {
        if (result.hasErrors()) {
//            model.addAttribute("users", userService.findAll(Pageable.unpaged()).getContent());
//            model.addAttribute("pcolors", pColorService.findAll());
            return "products/edit";
        }

        Product product = productService.findById(form.getId());
        if (product == null) {
            String errorMsg = URLEncoder.encode("Sản phẩm không tồn tại", "UTF-8");
            return "redirect:/products?error=" + errorMsg;
        }

        product.setName(form.getName());
        product.setDescription(form.getDescription());
        product.setUpdatedAt(LocalDateTime.now());

        // Convert favorites list to JSON string
        try {
            ObjectMapper mapper = new ObjectMapper();
            String favoriteJson = mapper.writeValueAsString(form.getFavorite());
            product.setFavorite(favoriteJson);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Xử lý màu sắc và upload ảnh (tương tự như store)
        List<Object> colorsToStore = new ArrayList<>();
        if (form.getColors() != null) {
            for (ProductColorForm colorForm : form.getColors()) {
                MultipartFile file = colorForm.getImg();
                String imagePath = "";
                try {
                    if (file != null && !file.isEmpty()) {
                        // Sinh tên file ngẫu nhiên sử dụng UUID
                        String fileName = StringUtils.cleanPath(file.getOriginalFilename());
                        String randomFileName = UUID.randomUUID().toString() + "_" + fileName;
                        File uploadDir = new File("img");
                        if (!uploadDir.exists()) {
                            uploadDir.mkdirs();
                        }
                        File dest = new File(uploadDir, randomFileName);
                        Files.copy(file.getInputStream(), dest.toPath(), StandardCopyOption.REPLACE_EXISTING);
                        imagePath = "/img/" + randomFileName;
                    } else {
                        // Nếu không có file mới, sử dụng ảnh cũ được gửi qua hidden field (existingImg)
                        imagePath = (colorForm.getExistingImg() != null && !colorForm.getExistingImg().isEmpty())
                                ? colorForm.getExistingImg()
                                : "/images/default-phone.png";
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                java.util.Map<String, Object> colorMap = new java.util.HashMap<>();
                colorMap.put("name", colorForm.getName());
                colorMap.put("money", colorForm.getMoney());
                colorMap.put("quantity", colorForm.getQuantity());
                if (colorForm.getMoneyDiscount() != null && !colorForm.getMoneyDiscount().equals("0")) {
                    colorMap.put("moneyDiscount", colorForm.getMoneyDiscount());
                }
                colorMap.put("img", imagePath);
                colorsToStore.add(colorMap);
            }
        }

        try {
            ObjectMapper mapper = new ObjectMapper();
            String colorsJson = mapper.writeValueAsString(colorsToStore);
            product.setColor(colorsJson);
        } catch (Exception e) {
            e.printStackTrace();
        }

        productService.save(product);
        String resultMsg = URLEncoder.encode("Sản phẩm đã được cập nhật thành công", "UTF-8");
        return "redirect:/products?success=" + resultMsg;
    }
}
