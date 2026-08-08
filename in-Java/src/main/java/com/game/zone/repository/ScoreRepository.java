package com.game.zone.repository;

import com.game.zone.model.Score;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ScoreRepository extends JpaRepository<Score, UUID> {
    // Get global leaderboard across all games
    @Query("SELECT new com.game.zone.dto.GlobalRankDTO(s.user.id, s.user.username, SUM(s.scoreValue)) " +
           "FROM Score s GROUP BY s.user.id, s.user.username ORDER BY SUM(s.scoreValue) DESC")
    List<com.game.zone.dto.GlobalRankDTO> findGlobalLeaderboard();

    // Get top 10 scores for a specific game (global leaderboard)
    @Query("SELECT s FROM Score s WHERE s.game.id = :gameId ORDER BY s.scoreValue DESC")
    List<Score> findTop10ByGameIdOrderByScoreDesc(@Param("gameId") UUID gameId);

    // Get all scores by a specific user
    List<Score> findByUserIdOrderByScoreValueDesc(UUID userId);

    // Get all scores for a specific game
    List<Score> findByGameIdOrderByScoreValueDesc(UUID gameId);

    Optional<Score> findByUserIdAndGameId(UUID userId, UUID gameId);

    // Get the highest score for a specific user and game (personal best)
    @Query("SELECT s FROM Score s WHERE s.user.id = :userId AND s.game.id = :gameId ORDER BY s.scoreValue DESC")
    List<Score> findTopByUserIdAndGameIdOrderByScoreDesc(@Param("userId") UUID userId,
            @Param("gameId") UUID gameId);
}