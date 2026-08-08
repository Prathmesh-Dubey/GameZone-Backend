package com.game.zone.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public class GameDTO {
    private UUID id;
    private String title;
    private String description;
    private String category;
    private String thumbnail;
    private Boolean active;
    private String gameCode; // 🔥 new
    private Boolean isDynamic; // 🔥 new (wrapper to allow null on update)
    private String type; // 🔥 game or simulator
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Constructors
    public GameDTO() {
    }

    public GameDTO(UUID id, String title, String description, String category,
            String thumbnail, Boolean active,
            String gameCode, Boolean isDynamic, String type,
            LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.category = category;
        this.thumbnail = thumbnail;
        this.active = active;
        this.gameCode = gameCode;
        this.isDynamic = isDynamic;
        this.type = type;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Getters and Setters
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

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public String getGameCode() {
        return gameCode;
    }

    public void setGameCode(String gameCode) {
        this.gameCode = gameCode;
    }

    public Boolean getIsDynamic() {
        return isDynamic;
    }

    public void setIsDynamic(Boolean isDynamic) {
        this.isDynamic = isDynamic;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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