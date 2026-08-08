package com.game.zone.repository;

import com.game.zone.model.GameSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface GameSessionRepository extends JpaRepository<GameSession, UUID> {

    // All sessions for a user, ordered by start time descending
    List<GameSession> findByUserIdOrderByStartTimeDesc(UUID userId);

    // All sessions for a game, ordered by start time descending
    List<GameSession> findByGameIdOrderByStartTimeDesc(UUID gameId);

    // Find active sessions (where endTime is null)
    List<GameSession> findByUserIdAndEndTimeIsNull(UUID userId);

    // Count sessions for a user
    long countByUserId(UUID userId);

    // Total play time (in seconds) for a user (sum of duration where endTime is not
    // null)
    @Query("SELECT COALESCE(SUM(s.duration), 0) FROM GameSession s WHERE s.user.id = :userId AND s.endTime IS NOT NULL")
    Long sumDurationByUserId(@Param("userId") UUID userId);

    // Count distinct users who have sessions in a given date range (daily active
    // users)
    @Query("SELECT COUNT(DISTINCT s.user.id) FROM GameSession s WHERE s.startTime BETWEEN :start AND :end")
    Long countDistinctUsersByDateRange(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);
}