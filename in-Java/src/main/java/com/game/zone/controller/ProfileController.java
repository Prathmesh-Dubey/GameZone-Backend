package com.game.zone.controller;

import com.game.zone.dto.ProfileRequestDTO;
import com.game.zone.dto.ProfileResponseDTO;
import com.game.zone.service.ProfileService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/profiles")
@CrossOrigin(origins = "*")
public class ProfileController {

    private final ProfileService profileService;

    public ProfileController(ProfileService profileService) {
        this.profileService = profileService;
    }

    // GET /api/profiles/{userId}
    @GetMapping("/{userId}")
    public ResponseEntity<ProfileResponseDTO> getProfile(@PathVariable UUID userId) {
        try {
            ProfileResponseDTO response = profileService.getProfileByUserId(userId);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            // If the exception message indicates "not found", return 404
            if (e.getMessage().contains("not found")) {
                return ResponseEntity.notFound().build();
            }
            // Otherwise re-throw (will become 500)
            throw e;
        }
    }

    // POST /api/profiles (create)
    @PostMapping
    public ResponseEntity<ProfileResponseDTO> createProfile(@RequestBody ProfileRequestDTO request) {
        ProfileResponseDTO response = profileService.createProfile(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // PUT /api/profiles/{userId} (update)
    @PutMapping("/{userId}")
    public ResponseEntity<ProfileResponseDTO> updateProfile(@PathVariable UUID userId,
            @RequestBody ProfileRequestDTO request) {
        try {
            ProfileResponseDTO response = profileService.updateProfile(userId, request);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            if (e.getMessage().contains("not found")) {
                return ResponseEntity.notFound().build();
            }
            throw e;
        }
    }

    // DELETE /api/profiles/{userId} (optional)
    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteProfile(@PathVariable UUID userId) {
        profileService.deleteProfile(userId);
        return ResponseEntity.noContent().build();
    }
}