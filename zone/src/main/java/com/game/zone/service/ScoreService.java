package com.game.zone.service;

import com.game.zone.dto.ScoreDTO;
import com.game.zone.model.Game;
import com.game.zone.model.Score;
import com.game.zone.model.User;
import com.game.zone.repository.GameRepository;
import com.game.zone.repository.ScoreRepository;
import com.game.zone.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ScoreService {

    private final ScoreRepository scoreRepository;
    private final UserRepository userRepository;
    private final GameRepository gameRepository;
    private final com.game.zone.repository.SimulatorRepository simulatorRepository;

    public ScoreService(ScoreRepository scoreRepository,
            UserRepository userRepository,
            GameRepository gameRepository,
            com.game.zone.repository.SimulatorRepository simulatorRepository) {
        this.scoreRepository = scoreRepository;
        this.userRepository = userRepository;
        this.gameRepository = gameRepository;
        this.simulatorRepository = simulatorRepository;
    }

    // Save a new score
    @Transactional
    public ScoreDTO saveScore(ScoreDTO dto) {
        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));
        Game game = gameRepository.findById(dto.getGameId())
                .orElseGet(() -> {
                    com.game.zone.model.Simulator sim = simulatorRepository.findById(dto.getGameId()).orElse(null);
                    if (sim != null) {
                        Game g = new Game();
                        g.setId(sim.getId());
                        g.setTitle(sim.getTitle());
                        g.setDescription(sim.getDescription());
                        g.setCategory(sim.getCategory());
                        g.setThumbnail(sim.getThumbnail());
                        g.setActive(sim.isActive());
                        g.setType("simulator");
                        g.setGameCode(sim.getSimulatorCode());
                        return gameRepository.save(g);
                    }
                    throw new RuntimeException("Game or Simulator not found");
                });

        // Look for existing row
        Score existing = scoreRepository.findByUserIdAndGameId(user.getId(), game.getId())
                .orElse(null);

        Score score;
        if (existing != null) {
            // Update only if new score is higher (you can also update unconditionally)
            if (dto.getScoreValue() > existing.getScoreValue()) {
                existing.setScoreValue(dto.getScoreValue());
                existing.setPlayedAt(LocalDateTime.now());
            }
            score = existing;
        } else {
            score = new Score();
            score.setScoreValue(dto.getScoreValue());
            score.setUser(user);
            score.setGame(game);
        }

        Score saved = scoreRepository.save(score);
        return toDTO(saved);
    }

    // Get top 10 scores for a game (leaderboard)
    public List<ScoreDTO> getTopScoresByGame(UUID gameId) {
        return scoreRepository.findTop10ByGameIdOrderByScoreDesc(gameId)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    // Get global leaderboard (Top players by sum of all scores)
    public List<com.game.zone.dto.GlobalRankDTO> getGlobalLeaderboard() {
        return scoreRepository.findGlobalLeaderboard();
    }

    // Get all scores for a specific user
    public List<ScoreDTO> getScoresByUser(UUID userId) {
        return scoreRepository.findByUserIdOrderByScoreValueDesc(userId)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    // Get all scores for a specific game
    public List<ScoreDTO> getScoresByGame(UUID gameId) {
        return scoreRepository.findByGameIdOrderByScoreValueDesc(gameId)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    // Get personal best for a user and game (the highest score)
    public ScoreDTO getPersonalBest(UUID userId, UUID gameId) {
        List<Score> scores = scoreRepository.findTopByUserIdAndGameIdOrderByScoreDesc(userId, gameId);
        if (scores.isEmpty()) {
            return null; // or throw an exception
        }
        return toDTO(scores.get(0));
    }

    // Mapper
    private ScoreDTO toDTO(Score score) {
        return new ScoreDTO(
                score.getId(),
                score.getScoreValue(),
                score.getPlayedAt(),
                score.getUser().getId(),
                score.getGame().getId());
    }
}