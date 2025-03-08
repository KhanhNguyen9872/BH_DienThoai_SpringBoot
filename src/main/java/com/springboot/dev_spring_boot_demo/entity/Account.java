package com.springboot.dev_spring_boot_demo.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "account")
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "username")
    private String username;

    @Column(name = "password")
    private String password;

    @Column(name = "lock_acc")
    private Boolean lockAcc;

    // Many-to-one relationship to UserInfo (via user_id)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private UserInfo user;

    public Account() {
    }

    public Account(String username, String password, Boolean lockAcc, UserInfo user) {
        this.username = username;
        this.password = password;
        this.lockAcc = lockAcc;
        this.user = user;
    }

    // Getters and setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Boolean getLockAcc() {
        return lockAcc;
    }

    public void setLockAcc(Boolean lockAcc) {
        this.lockAcc = lockAcc;
    }

    public UserInfo getUser() {
        return user;
    }

    public void setUser(UserInfo user) {
        this.user = user;
    }

    @Override
    public String toString() {
        return "Account{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", lockAcc=" + lockAcc +
                ", user=" + (user != null ? user.getId() : null) +
                '}';
    }
}
