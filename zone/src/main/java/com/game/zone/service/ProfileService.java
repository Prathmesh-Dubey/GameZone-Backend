package com.game.zone.service;

import com.game.zone.dto.ProfileRequestDTO;
import com.game.zone.dto.ProfileResponseDTO;
import com.game.zone.model.Profile;
import com.game.zone.model.User;
import com.game.zone.repository.ProfileRepository;
import com.game.zone.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class ProfileService {

    private final ProfileRepository profileRepository;
    private final UserRepository userRepository;

    public ProfileService(ProfileRepository profileRepository,
            UserRepository userRepository) {
        this.profileRepository = profileRepository;
        this.userRepository = userRepository;
    }

    public ProfileResponseDTO getProfileByUserId(UUID userId) {
        Profile profile = profileRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Profile not found for user: " + userId));
        return mapToResponse(profile);
    }

    @Transactional
    public ProfileResponseDTO createProfile(ProfileRequestDTO request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (profileRepository.findByUserId(user.getId()).isPresent()) {
            throw new RuntimeException("Profile already exists for user: " + user.getId());
        }

        Profile profile = new Profile();
        profile.setUser(user);
        profile.setUsername(user.getUsername());

        // Set basic fields
        profile.setBio(request.getBio());
        profile.setAvatarUrl(request.getAvatarUrl());
        profile.setLocation(request.getLocation());
        profile.setDateOfBirth(request.getDateOfBirth());
        profile.setWebsite(request.getWebsite());

        // Set new fields
        profile.setAccentColor(request.getAccentColor());
        profile.setAvatarSeed(request.getAvatarSeed());

        Profile saved = profileRepository.save(profile);
        return mapToResponse(saved);
    }

    @Transactional
    public ProfileResponseDTO updateProfile(UUID userId, ProfileRequestDTO request) {
        Profile profile = profileRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Profile not found for user: " + userId));

        // Update basic fields (allow null to keep existing)
        if (request.getBio() != null)
            profile.setBio(request.getBio());
        if (request.getAvatarUrl() != null)
            profile.setAvatarUrl(request.getAvatarUrl());
        if (request.getLocation() != null)
            profile.setLocation(request.getLocation());
        if (request.getDateOfBirth() != null)
            profile.setDateOfBirth(request.getDateOfBirth());
        if (request.getWebsite() != null)
            profile.setWebsite(request.getWebsite());

        // Update new fields (allow null to keep existing)
        if (request.getAccentColor() != null)
            profile.setAccentColor(request.getAccentColor());
        if (request.getAvatarSeed() != null)
            profile.setAvatarSeed(request.getAvatarSeed());

        Profile updated = profileRepository.save(profile);
        return mapToResponse(updated);
    }

    @Transactional
    public void deleteProfile(UUID userId) {
        Profile profile = profileRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Profile not found for user: " + userId));
        profileRepository.delete(profile);
    }

    private ProfileResponseDTO mapToResponse(Profile profile) {
        return new ProfileResponseDTO(
                profile.getId(),
                profile.getUser().getId(),
                profile.getBio(),
                profile.getAvatarUrl(),
                profile.getLocation(),
                profile.getDateOfBirth(),
                profile.getWebsite(),
                profile.getAccentColor(),
                profile.getAvatarSeed(),
                profile.getCreatedAt(),
                profile.getUpdatedAt());
    }
}