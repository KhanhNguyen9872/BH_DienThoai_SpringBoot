package com.springboot.dev_spring_boot_demo.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.web.multipart.MultipartFile;

public class ProductColorForm {
    private String name;
    private String money;
    private int quantity;
    private String moneyDiscount; // Có thể null

    // Khi tạo mới, trường này dùng để nhận file upload
    @JsonIgnore
    private MultipartFile img;

    // Khi chỉnh sửa, trường này sẽ chứa đường dẫn ảnh đã lưu
    @JsonProperty("img")
    private String existingImg;

    // Getters và setters
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getMoney() {
        return money;
    }
    public void setMoney(String money) {
        this.money = money;
    }
    public int getQuantity() {
        return quantity;
    }
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
    public String getMoneyDiscount() {
        return moneyDiscount;
    }
    public void setMoneyDiscount(String moneyDiscount) {
        this.moneyDiscount = moneyDiscount;
    }
    public MultipartFile getImg() {
        return img;
    }
    public void setImg(MultipartFile img) {
        this.img = img;
    }
    public String getExistingImg() {
        return existingImg;
    }
    public void setExistingImg(String existingImg) {
        this.existingImg = existingImg;
    }
}
