package com.game.zone.service;

import com.game.zone.dto.UserDTO;
import com.game.zone.model.Profile;
import com.game.zone.model.User;
import com.game.zone.repository.ProfileRepository;
import com.game.zone.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;
import java.util.stream.Collectors;
import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final ProfileRepository profileRepository;

    public UserService(UserRepository userRepository,
            ProfileRepository profileRepository) {
        this.userRepository = userRepository;
        this.profileRepository = profileRepository;
    }

    public List<UserDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::toUserDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get user by ID (with profile).
     */
    public UserDTO getUserById(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return toUserDTO(user);
    }

    /**
     * Update user. Only provided fields are changed.
     * If password is provided, it is updated (plain text).
     */
    @Transactional
    public UserDTO updateUser(UUID userId, UserDTO dto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Update username if provided and not already taken
        if (dto.getUsername() != null && !dto.getUsername().isEmpty()) {
            if (!user.getUsername().equals(dto.getUsername())
                    && userRepository.existsByUsername(dto.getUsername())) {
                throw new RuntimeException("Username already taken");
            }
            user.setUsername(dto.getUsername());
        }

        // Update email if provided and not already taken
        if (dto.getEmail() != null && !dto.getEmail().isEmpty()) {
            if (!user.getEmail().equals(dto.getEmail())
                    && userRepository.existsByEmail(dto.getEmail())) {
                throw new RuntimeException("Email already taken");
            }
            user.setEmail(dto.getEmail());
        }

        // Update password if provided (plain text)
        if (dto.getPassword() != null && !dto.getPassword().isEmpty()) {
            user.setPassword(dto.getPassword());
        }

        // Update role if provided
        if (dto.getRole() != null && !dto.getRole().isEmpty()) {
            user.setRole(dto.getRole());
        }

        User updated = userRepository.save(user);
        return toUserDTO(updated);
    }

    /**
     * Delete a user.
     */
    @Transactional
    public void deleteUser(UUID userId) {
        if (!userRepository.existsById(userId)) {
            throw new RuntimeException("User not found");
        }
        userRepository.deleteById(userId);
    }

    /**
     * Helper to get a raw User entity (used by other services).
     */
    public User findRawUserById(UUID userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    /**
     * Mapper: converts User (+ Profile) to UserDTO.
     * Password is always set to null for security.
     */
    private UserDTO toUserDTO(User user) {
        Profile profile = profileRepository.findByUserId(user.getId()).orElse(null);

        return new UserDTO(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getRole(),
                user.getCreatedAt(),
                null, // ← password is never returned
                profile != null ? profile.getId() : null,
                profile != null ? profile.getBio() : null,
                profile != null ? profile.getAvatarUrl() : null,
                profile != null ? profile.getLocation() : null,
                profile != null ? profile.getDateOfBirth() : null,
                profile != null ? profile.getWebsite() : null,
                profile != null ? profile.getAccentColor() : null,
                profile != null ? profile.getAvatarSeed() : null);
    }
}