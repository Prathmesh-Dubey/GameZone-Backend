package com.game.zone.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public class UserDTO {
    private UUID id;
    private String username;
    private String email;
    private String role;
    private LocalDateTime createdAt;
    private String password; // used only for requests (update/register)
    private UUID profileId;
    private String bio;
    private String avatarUrl;
    private String location;
    private LocalDate dateOfBirth;
    private String website;
    private String accentColor;
    private String avatarSeed;

    // All-args constructor (order must match usage)
    public UserDTO(UUID id, String username, String email, String role,
            LocalDateTime createdAt, String password,
            UUID profileId, String bio, String avatarUrl,
            String location, LocalDate dateOfBirth, String website,
            String accentColor, String avatarSeed) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.role = role;
        this.createdAt = createdAt;
        this.password = password;
        this.profileId = profileId;
        this.bio = bio;
        this.avatarUrl = avatarUrl;
        this.location = location;
        this.dateOfBirth = dateOfBirth;
        this.website = website;
        this.accentColor = accentColor;
        this.avatarSeed = avatarSeed;
    }

    // No-args constructor (for Jackson deserialization)
    public UserDTO() {
    }

    // Getters and Setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public UUID getProfileId() {
        return profileId;
    }

    public void setProfileId(UUID profileId) {
        this.profileId = profileId;
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