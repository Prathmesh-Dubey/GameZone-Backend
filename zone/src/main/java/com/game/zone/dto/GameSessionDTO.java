package com.game.zone.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public class GameSessionDTO {
    private UUID id;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Integer duration; // seconds
    private UUID userId;
    private UUID gameId;

    public GameSessionDTO() {
    }

    public GameSessionDTO(UUID id, LocalDateTime startTime, LocalDateTime endTime,
            Integer duration, UUID userId, UUID gameId) {
        this.id = id;
        this.startTime = startTime;
        this.endTime = endTime;
        this.duration = duration;
        this.userId = userId;
        this.gameId = gameId;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
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