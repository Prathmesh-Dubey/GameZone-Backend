package com.game.zone.service;

import com.game.zone.dto.AchievementDTO;
import com.game.zone.model.Achievement;
import com.game.zone.repository.AchievementRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class AchievementService {

    private final AchievementRepository achievementRepository;

    // Explicit constructor
    public AchievementService(AchievementRepository achievementRepository) {
        this.achievementRepository = achievementRepository;
    }

    public List<AchievementDTO> getAllAchievements() {
        return achievementRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public AchievementDTO getAchievementById(UUID id) {
        Achievement achievement = achievementRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Achievement not found"));
        return toDTO(achievement);
    }

    @Transactional
    public AchievementDTO createAchievement(AchievementDTO dto) {
        Achievement achievement = new Achievement();
        achievement.setTitle(dto.getTitle());
        achievement.setDescription(dto.getDescription());
        achievement.setRequiredScore(dto.getRequiredScore());
        Achievement saved = achievementRepository.save(achievement);
        return toDTO(saved);
    }

    @Transactional
    public AchievementDTO updateAchievement(UUID id, AchievementDTO dto) {
        Achievement achievement = achievementRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Achievement not found"));
        if (dto.getTitle() != null)
            achievement.setTitle(dto.getTitle());
        if (dto.getDescription() != null)
            achievement.setDescription(dto.getDescription());
        if (dto.getRequiredScore() > 0)
            achievement.setRequiredScore(dto.getRequiredScore());
        Achievement updated = achievementRepository.save(achievement);
        return toDTO(updated);
    }

    public void deleteAchievement(UUID id) {
        achievementRepository.deleteById(id);
    }

    private AchievementDTO toDTO(Achievement achievement) {
        return new AchievementDTO(
                achievement.getId(),
                achievement.getTitle(),
                achievement.getDescription(),
                achievement.getRequiredScore(),
                achievement.getCreatedAt(),
                achievement.getUpdatedAt());
    }
}