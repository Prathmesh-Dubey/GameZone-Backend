package com.game.zone.controller;

import com.game.zone.dto.UserAchievementDTO;
import com.game.zone.service.UserAchievementService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/user-achievements")
@CrossOrigin(origins = "*")
public class UserAchievementController {

    private final UserAchievementService userAchievementService;

    public UserAchievementController(UserAchievementService userAchievementService) {
        this.userAchievementService = userAchievementService;
    }

    // POST /api/user-achievements/unlock
    @PostMapping("/unlock")
    public ResponseEntity<UserAchievementDTO> unlockAchievement(@RequestParam UUID userId,
            @RequestParam UUID achievementId) {
        UserAchievementDTO unlocked = userAchievementService.unlockAchievement(userId, achievementId);
        return ResponseEntity.status(HttpStatus.CREATED).body(unlocked);
    }

    // GET /api/user-achievements/user/{userId}
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<UserAchievementDTO>> getByUser(@PathVariable UUID userId) {
        return ResponseEntity.ok(userAchievementService.getAchievementsForUser(userId));
    }

    // GET /api/user-achievements/achievement/{achievementId}
    @GetMapping("/achievement/{achievementId}")
    public ResponseEntity<List<UserAchievementDTO>> getByAchievement(@PathVariable UUID achievementId) {
        return ResponseEntity.ok(userAchievementService.getUsersForAchievement(achievementId));
    }

    // GET /api/user-achievements/count/user/{userId}
    @GetMapping("/count/user/{userId}")
    public ResponseEntity<Long> getCountForUser(@PathVariable UUID userId) {
        return ResponseEntity.ok(userAchievementService.getAchievementCountForUser(userId));
    }

    // POST /api/user-achievements/check?userId=...&score=...
    @PostMapping("/check")
    public ResponseEntity<List<UserAchievementDTO>> checkAndUnlock(@RequestParam UUID userId,
            @RequestParam int score) {
        List<UserAchievementDTO> unlocked = userAchievementService.checkAndUnlockAchievements(userId, score);
        return ResponseEntity.ok(unlocked);
    }
}