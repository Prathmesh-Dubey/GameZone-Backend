package com.game.zone.dto;

import java.util.UUID;

public class GlobalRankDTO {
    private UUID userId;
    private String username;
    private Long totalScore;

    public GlobalRankDTO(UUID userId, String username, Long totalScore) {
        this.userId = userId;
        this.username = username;
        this.totalScore = totalScore;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Long getTotalScore() {
        return totalScore;
    }

    public void setTotalScore(Long totalScore) {
        this.totalScore = totalScore;
    }
}
