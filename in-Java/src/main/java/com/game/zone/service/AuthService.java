package com.game.zone.service;

import com.game.zone.dto.LoginRequest;
import com.game.zone.dto.RegisterRequest;
import com.game.zone.dto.UserDTO;
import com.game.zone.model.Profile;
import com.game.zone.model.User;
import com.game.zone.repository.ProfileRepository;
import com.game.zone.repository.UserRepository;

import java.util.stream.Collectors;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final ProfileRepository profileRepository;

    public AuthService(UserRepository userRepository, ProfileRepository profileRepository) {
        this.userRepository = userRepository;
        this.profileRepository = profileRepository;
    }

    /**
     * Login – accepts username OR email in the "identifier" field.
     */
    public UserDTO login(LoginRequest request) {
        String loginValue = request.getIdentifier(); // ✅ use getIdentifier()
        User user;

        if (loginValue.contains("@")) {
            user = userRepository.findByEmail(loginValue)
                    .orElseThrow(() -> new RuntimeException("Invalid email or password"));
        } else {
            user = userRepository.findByUsername(loginValue)
                    .orElseThrow(() -> new RuntimeException("Invalid username or password"));
        }

        if (!user.getPassword().equals(request.getPassword())) {
            throw new RuntimeException("Invalid password");
        }

        return toUserDTO(user);
    }

    /**
     * Register – creates a new user and an associated profile with username.
     */
    @Transactional
    public UserDTO register(RegisterRequest request) {
        if ("ADMIN".equalsIgnoreCase(request.getRole())) {
            if (!"prathm123".equals(request.getAdminKey())) {
                throw new RuntimeException("Invalid admin registration key");
            }
        }

        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Username already taken");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already taken");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(request.getPassword());
        user.setRole(request.getRole() != null ? request.getRole() : "USER");

        User saved = userRepository.save(user);

        // Create profile and set its username to match the user's username
        Profile profile = new Profile();
        profile.setUser(saved);
        profile.setUsername(saved.getUsername()); // ✅ set username
        profileRepository.save(profile);

        return toUserDTO(saved);
    }

    public List<UserDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::toUserDTO)
                .collect(Collectors.toList());
    }

    public UserDTO getUserByIdentifier(String identifier) {
        User user;
        if (identifier.contains("@")) {
            user = userRepository.findByEmail(identifier)
                    .orElseThrow(() -> new RuntimeException("User not found"));
        } else {
            user = userRepository.findByUsername(identifier)
                    .orElseThrow(() -> new RuntimeException("User not found"));
        }
        return toUserDTO(user);
    }

    /**
     * Mapper – converts User (+ Profile) to UserDTO, password never returned.
     */
    private UserDTO toUserDTO(User user) {
        Profile profile = profileRepository.findByUserId(user.getId()).orElse(null);

        return new UserDTO(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getRole(),
                user.getCreatedAt(),
                null, // password never returned
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