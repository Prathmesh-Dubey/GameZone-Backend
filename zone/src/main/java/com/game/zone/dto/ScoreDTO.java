package com.game.zone.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public class ScoreDTO {
    private UUID id;
    private int scoreValue;
    private LocalDateTime playedAt;
    private UUID userId;
    private UUID gameId;

    public ScoreDTO() {
    }

    public ScoreDTO(UUID id, int scoreValue, LocalDateTime playedAt, UUID userId, UUID gameId) {
        this.id = id;
        this.scoreValue = scoreValue;
        this.playedAt = playedAt;
        this.userId = userId;
        this.gameId = gameId;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public int getScoreValue() {
        return scoreValue;
    }

    public void setScoreValue(int scoreValue) {
        this.scoreValue = scoreValue;
    }

    public LocalDateTime getPlayedAt() {
        return playedAt;
    }

    public void setPlayedAt(LocalDateTime playedAt) {
        this.playedAt = playedAt;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public UUID getGameId() {
        return gameId;
    }

    public void setGameId(UUID gameId) {
        this.gameId = gameId;
    }
}