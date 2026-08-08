package com.game.zone.repository;

import com.game.zone.model.UserAchievement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface UserAchievementRepository extends JpaRepository<UserAchievement, UUID> {
    List<UserAchievement> findByUserId(UUID userId);

    List<UserAchievement> findByAchievementId(UUID achievementId);

    boolean existsByUserIdAndAchievementId(UUID userId, UUID achievementId);

    long countByUserId(UUID userId);
}