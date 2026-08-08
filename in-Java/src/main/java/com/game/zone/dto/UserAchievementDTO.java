package com.game.zone.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public class UserAchievementDTO {
    private UUID id;
    private UUID userId;
    private UUID achievementId;
    private String achievementTitle;
    private String achievementDescription;
    private LocalDateTime unlockedAt;

    public UserAchievementDTO() {
    }

    public UserAchievementDTO(UUID id, UUID userId, UUID achievementId,
            String achievementTitle, String achievementDescription, LocalDateTime unlockedAt) {
        this.id = id;
        this.userId = userId;
        this.achievementId = achievementId;
        this.achievementTitle = achievementTitle;
        this.achievementDescription = achievementDescription;
        this.unlockedAt = unlockedAt;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public UUID getAchievementId() {
        return achievementId;
    }

    public void setAchievementId(UUID achievementId) {
        this.achievementId = achievementId;
    }

    public String getAchievementTitle() {
        return achievementTitle;
    }

    public void setAchievementTitle(String achievementTitle) {
        this.achievementTitle = achievementTitle;
    }

    public LocalDateTime getUnlockedAt() {
        return unlockedAt;
    }

    public void setUnlockedAt(LocalDateTime unlockedAt) {
        this.unlockedAt = unlockedAt;
    }

    public String getAchievementDescription() {
        return achievementDescription;
    }

    public void setAchievementDescription(String achievementDescription) {
        this.achievementDescription = achievementDescription;
    }
}