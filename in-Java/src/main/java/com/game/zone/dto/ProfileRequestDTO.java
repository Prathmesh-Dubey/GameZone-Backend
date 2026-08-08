package com.game.zone.dto;

import java.time.LocalDate;
import java.util.UUID;

public class ProfileRequestDTO {
    private UUID userId;
    private String bio;
    private String avatarUrl;
    private String location;
    private LocalDate dateOfBirth;
    private String website;
    private String accentColor; // NEW
    private String avatarSeed; // NEW

    public ProfileRequestDTO() {
    }

    public ProfileRequestDTO(UUID userId, String bio, String avatarUrl, String location,
            LocalDate dateOfBirth, String website,
            String accentColor, String avatarSeed) {
        this.userId = userId;
        this.bio = bio;
        this.avatarUrl = avatarUrl;
        this.location = location;
        this.dateOfBirth = dateOfBirth;
        this.website = website;
        this.accentColor = accentColor;
        this.avatarSeed = avatarSeed;
    }

    // Getters and Setters
    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getAccentColor() {
        return accentColor;
    }

    public void setAccentColor(String accentColor) {
        this.accentColor = accentColor;
    }

    public String getAvatarSeed() {
        return avatarSeed;
    }

    public void setAvatarSeed(String avatarSeed) {
        this.avatarSeed = avatarSeed;
    }
}