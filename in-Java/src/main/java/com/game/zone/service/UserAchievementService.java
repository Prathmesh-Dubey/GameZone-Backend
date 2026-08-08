package com.game.zone.service;

import com.game.zone.dto.UserAchievementDTO;
import com.game.zone.model.Achievement;
import com.game.zone.model.User;
import com.game.zone.model.UserAchievement;
import com.game.zone.repository.AchievementRepository;
import com.game.zone.repository.UserAchievementRepository;
import com.game.zone.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class UserAchievementService {

    private final UserAchievementRepository userAchievementRepository;
    private final UserRepository userRepository;
    private final AchievementRepository achievementRepository;

    public UserAchievementService(UserAchievementRepository userAchievementRepository,
            UserRepository userRepository,
            AchievementRepository achievementRepository) {
        this.userAchievementRepository = userAchievementRepository;
        this.userRepository = userRepository;
        this.achievementRepository = achievementRepository;
    }

    // Unlock an achievement for a user (if not already unlocked)
    @Transactional
    public UserAchievementDTO unlockAchievement(UUID userId, UUID achievementId) {
        // Check if already unlocked
        if (userAchievementRepository.existsByUserIdAndAchievementId(userId, achievementId)) {
            throw new RuntimeException("Achievement already unlocked for this user");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Achievement achievement = achievementRepository.findById(achievementId)
                .orElseThrow(() -> new RuntimeException("Achievement not found"));

        UserAchievement userAchievement = new UserAchievement();
        userAchievement.setUser(user);
        userAchievement.setAchievement(achievement);
        // unlockedAt will be auto-set by @CreationTimestamp

        UserAchievement saved = userAchievementRepository.save(userAchievement);
        return toDTO(saved);
    }

    // Get all achievements unlocked by a user
    public List<UserAchievementDTO> getAchievementsForUser(UUID userId) {
        return userAchievementRepository.findByUserId(userId).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    // Get all users who have unlocked a specific achievement
    public List<UserAchievementDTO> getUsersForAchievement(UUID achievementId) {
        return userAchievementRepository.findByAchievementId(achievementId).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    // Get total unlocked achievements count for a user
    public long getAchievementCountForUser(UUID userId) {
        return userAchievementRepository.countByUserId(userId);
    }

    // (Optional) Auto-unlock achievements based on score –
    // This can be called after submitting a score.
    // We'll implement a basic version that checks if any achievement has
    // requiredScore <= score,
    // and then unlocks them.

    @Transactional
    public List<UserAchievementDTO> checkAndUnlockAchievements(UUID userId, int score) {
        // Get all achievements not yet unlocked by this user
        List<Achievement> allAchievements = achievementRepository.findAll();
        List<UUID> unlockedAchievementIds = userAchievementRepository.findByUserId(userId)
                .stream()
                .map(ua -> ua.getAchievement().getId())
                .collect(Collectors.toList());

        List<Achievement> achievable = allAchievements.stream()
                .filter(a -> a.getRequiredScore() <= score)
                .filter(a -> !unlockedAchievementIds.contains(a.getId()))
                .collect(Collectors.toList());

        List<UserAchievementDTO> unlocked = achievable.stream()
                .map(a -> unlockAchievement(userId, a.getId()))
                .collect(Collectors.toList());

        return unlocked;
    }

    private UserAchievementDTO toDTO(UserAchievement userAchievement) {
        return new UserAchievementDTO(
                userAchievement.getId(),
                userAchievement.getUser().getId(),
                userAchievement.getAchievement().getId(),
                userAchievement.getAchievement().getTitle(),
                userAchievement.getAchievement().getDescription(),
                userAchievement.getUnlockedAt());
    }
}