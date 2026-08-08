package com.game.zone.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public class NotificationDTO {
    private UUID id;
    private String title;
    private String message;
    private String type;
    private String createdByUsername;
    private LocalDateTime createdAt;
    private boolean active;
    private LocalDateTime expiresAt;

    public NotificationDTO() {}

    public NotificationDTO(UUID id, String title, String message, String type, String createdByUsername, LocalDateTime createdAt, boolean active, LocalDateTime expiresAt) {
        this.id = id;
        this.title = title;
        this.message = message;
        this.type = type;
        this.createdByUsername = createdByUsername;
        this.createdAt = createdAt;
        this.active = active;
        this.expiresAt = expiresAt;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getCreatedByUsername() { return createdByUsername; }
    public void setCreatedByUsername(String createdByUsername) { this.createdByUsername = createdByUsername; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    public LocalDateTime getExpiresAt() { return expiresAt; }
    public void setExpiresAt(LocalDateTime expiresAt) { this.expiresAt = expiresAt; }
}
