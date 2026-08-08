package com.game.zone.dto;

import java.time.LocalDateTime;

public class NotificationRequestDTO {
    private String title;
    private String message;
    private String type;
    private LocalDateTime expiresAt;

    public NotificationRequestDTO() {}

    public NotificationRequestDTO(String title, String message, String type, LocalDateTime expiresAt) {
        this.title = title;
        this.message = message;
        this.type = type;
        this.expiresAt = expiresAt;
    }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public LocalDateTime getExpiresAt() { return expiresAt; }
    public void setExpiresAt(LocalDateTime expiresAt) { this.expiresAt = expiresAt; }
}
