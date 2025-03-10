package com.springboot.dev_spring_boot_demo.dto;

import java.util.List;

public class ProductForm {
    private int id;
    private String name;
    private String description;
    private List<Integer> favorite;
    private List<ProductColorForm> colors;

    // Getters and setters
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public List<Integer> getFavorite() {
        return favorite;
    }
    public void setFavorite(List<Integer> favorite) {
        this.favorite = favorite;
    }
    public List<ProductColorForm> getColors() {
        return colors;
    }
    public void setColors(List<ProductColorForm> colors) {
        this.colors = colors;
    }
}
