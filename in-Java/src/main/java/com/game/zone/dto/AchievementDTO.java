package com.game.zone.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public class AchievementDTO {
    private UUID id;
    private String title;
    private String description;
    private int requiredScore;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public AchievementDTO() {
    }

    public AchievementDTO(UUID id, String title, String description,
            int requiredScore, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.requiredScore = requiredScore;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getRequiredScore() {
        return requiredScore;
    }

    public void setRequiredScore(int requiredScore) {
        this.requiredScore = requiredScore;
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
}