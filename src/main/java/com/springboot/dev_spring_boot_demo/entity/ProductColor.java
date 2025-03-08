package com.springboot.dev_spring_boot_demo.entity;

public class ProductColor {
    private String img;
    private String name;
    private String money;
    private int quantity;
    private String moneyDiscount; // This field is optional

    // Getters and setters
    public String getImg() {
        return img;
    }
    public void setImg(String img) {
        this.img = img;
    }

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
}