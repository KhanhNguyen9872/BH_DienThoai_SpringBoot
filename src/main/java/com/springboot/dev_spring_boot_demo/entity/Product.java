package com.springboot.dev_spring_boot_demo.entity;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "product")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "name")
    private String name;

    @Column(name = "description")
    private String description;

    // You may store arrays as JSON or comma-separated strings
    @Column(name = "favorite", columnDefinition = "TEXT")
    private String favorite;

    @Column(name = "color", columnDefinition = "TEXT")
    private String color;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public Product() {
    }

    public Product(String name, String description, String favorite, String color) {
        this.name = name;
        this.description = description;
        this.favorite = favorite;
        this.color = color;
    }

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

    public String getFavorite() {
        return favorite;
    }

    public void setFavorite(String favorite) {
        this.favorite = favorite;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    private List<Integer> toFavouritesArray() {
        ObjectMapper mapper = new ObjectMapper();

        try {
            // Parse JSON string into a list of Integers
            List<Integer> numbers = mapper.readValue(this.favorite, new TypeReference<List<Integer>>() {});

            // Print the list
            numbers.forEach(n -> System.out.print(n + " "));
            return numbers;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    private List<ProductColor> toProductColorsObject() {
        try {
            // Create ObjectMapper instance
            ObjectMapper mapper = new ObjectMapper();
            // Register module to handle Java 8 date/time types
            mapper.registerModule(new JavaTimeModule());

            // Parse JSON string into Product object
            List<ProductColor> productVariants = mapper.readValue(this.color, new TypeReference<List<ProductColor>>() {});

            return productVariants;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public long getFavouriteCount() {
        List<Integer> favourites = this.toFavouritesArray();
        if (favourites == null || favourites.size() == 0) {
            return 0;
        }

        return favourites.size();
    }

    public String getDefaultImg() {
        List<ProductColor> productVariants = this.toProductColorsObject();
        if (productVariants == null || productVariants.isEmpty()) {
            return "";
        }

        return productVariants.get(0).getImg();
    }

    public String getAllColorName() {
        List<ProductColor> productVariants = this.toProductColorsObject();
        if (productVariants == null || productVariants.isEmpty()) {
            return "";
        }

        String colorName = "";
        for (ProductColor productColor : productVariants) {
            colorName += productColor.getName() + ", ";
        }

        return colorName.substring(0, colorName.length() - 2);
    }

    @Override
    public String toString() {
        return "Product{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", favorite='" + favorite + '\'' +
                ", color='" + color + '\'' +
                '}';
    }

}
