package com.springboot.dev_spring_boot_demo.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "voucher")
public class Voucher {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "code")
    private String code;

    @Column(name = "discount")
    private BigDecimal discount;

    @Column(name = "count")
    private Integer count;

    // Stored as JSON text; use a converter if needed to convert to a List or array.
    @Column(name = "limit_user", columnDefinition = "TEXT")
    private String limitUser;

    // Stored as JSON text; use a converter if needed to convert to a List or array.
    @Column(name = "user_id", columnDefinition = "TEXT")
    private String userId;

    public Voucher() {
    }

    public Voucher(String code, BigDecimal discount, Integer count, String limitUser, String userId) {
        this.code = code;
        this.discount = discount;
        this.count = count;
        this.limitUser = limitUser;
        this.userId = userId;
    }

    // Getters and setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public BigDecimal getDiscount() {
        return discount;
    }

    public void setDiscount(BigDecimal discount) {
        this.discount = discount;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public String getLimitUser() {
        return limitUser;
    }

    public void setLimitUser(String limitUser) {
        this.limitUser = limitUser;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    @Override
    public String toString() {
        return "Voucher{" +
                "id=" + id +
                ", code='" + code + '\'' +
                ", discount=" + discount +
                ", count=" + count +
                ", limitUser='" + limitUser + '\'' +
                ", userId='" + userId + '\'' +
                '}';
    }
}
