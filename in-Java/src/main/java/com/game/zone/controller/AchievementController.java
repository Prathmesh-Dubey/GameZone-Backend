package com.game.zone.controller;

import com.game.zone.dto.AchievementDTO;
import com.game.zone.service.AchievementService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/achievements")
public class AchievementController {

    private final AchievementService achievementService;

    // Explicit constructor
    public AchievementController(AchievementService achievementService) {
        this.achievementService = achievementService;
    }

    @GetMapping
    public ResponseEntity<List<AchievementDTO>> getAll() {
        return ResponseEntity.ok(achievementService.getAllAchievements());
    }

    @GetMapping("/{id}")
    public ResponseEntity<AchievementDTO> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(achievementService.getAchievementById(id));
    }

    @PostMapping
    public ResponseEntity<AchievementDTO> create(@RequestBody AchievementDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(achievementService.createAchievement(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AchievementDTO> update(@PathVariable UUID id, @RequestBody AchievementDTO dto) {
        return ResponseEntity.ok(achievementService.updateAchievement(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        achievementService.deleteAchievement(id);
        return ResponseEntity.noContent().build();
    }
}