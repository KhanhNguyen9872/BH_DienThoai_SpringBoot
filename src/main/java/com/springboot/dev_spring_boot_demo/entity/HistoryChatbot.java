package com.springboot.dev_spring_boot_demo.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "history_chatbot")
public class HistoryChatbot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Many-to-one relationship with UserInfo.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private UserInfo user;

    @Column(name = "message")
    private String message;

    @Column(name = "isBot")
    private Boolean isBot;

    @Column(name = "time")
    private LocalDateTime time;

    public HistoryChatbot() {
    }

    // Getters and setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public UserInfo getUser() {
        return user;
    }

    public void setUser(UserInfo user) {
        this.user = user;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Boolean getIsBot() {
        return isBot;
    }

    public void setIsBot(Boolean isBot) {
        this.isBot = isBot;
    }

    public LocalDateTime getTime() {
        return time;
    }

    public void setTime(LocalDateTime time) {
        this.time = time;
    }

    @Override
    public String toString() {
        return "HistoryChatbot{" +
                "id=" + id +
                ", user=" + (user != null ? user.getId() : null) +
                ", message='" + message + '\'' +
                ", isBot=" + isBot +
                ", time=" + time +
                '}';
    }
}
