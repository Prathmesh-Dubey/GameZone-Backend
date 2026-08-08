package com.game.zone.service;

import com.game.zone.dto.GameSessionDTO;
import com.game.zone.model.Game;
import com.game.zone.model.GameSession;
import com.game.zone.model.User;
import com.game.zone.repository.GameRepository;
import com.game.zone.repository.GameSessionRepository;
import com.game.zone.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class GameSessionService {

    private final GameSessionRepository sessionRepository;
    private final UserRepository userRepository;
    private final GameRepository gameRepository;
    private final com.game.zone.repository.SimulatorRepository simulatorRepository;

    public GameSessionService(GameSessionRepository sessionRepository,
            UserRepository userRepository,
            GameRepository gameRepository,
            com.game.zone.repository.SimulatorRepository simulatorRepository) {
        this.sessionRepository = sessionRepository;
        this.userRepository = userRepository;
        this.gameRepository = gameRepository;
        this.simulatorRepository = simulatorRepository;
    }

    // Start a new session
    @Transactional
    public GameSessionDTO startSession(UUID userId, UUID gameId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Game game = gameRepository.findById(gameId)
                .orElseGet(() -> {
                    com.game.zone.model.Simulator sim = simulatorRepository.findById(gameId).orElse(null);
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

        GameSession session = new GameSession();
        session.setUser(user);
        session.setGame(game);
        session.setStartTime(LocalDateTime.now());
        // endTime and duration remain null until ended

        GameSession saved = sessionRepository.save(session);
        return toDTO(saved);
    }

    // End an existing session (set endTime and calculate duration)
    @Transactional
    public GameSessionDTO endSession(UUID sessionId) {
        GameSession session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new RuntimeException("Session not found"));

        if (session.getEndTime() != null) {
            throw new RuntimeException("Session already ended");
        }

        LocalDateTime now = LocalDateTime.now();
        session.setEndTime(now);

        // Calculate duration in seconds
        long seconds = Duration.between(session.getStartTime(), now).getSeconds();
        session.setDuration((int) seconds);

        GameSession updated = sessionRepository.save(session);
        return toDTO(updated);
    }

    // Get all sessions for a user
    public List<GameSessionDTO> getSessionsByUser(UUID userId) {
        return sessionRepository.findByUserIdOrderByStartTimeDesc(userId)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    // Get all sessions for a game
    public List<GameSessionDTO> getSessionsByGame(UUID gameId) {
        return sessionRepository.findByGameIdOrderByStartTimeDesc(gameId)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    // Get active sessions for a user (where endTime is null)
    public List<GameSessionDTO> getActiveSessionsByUser(UUID userId) {
        return sessionRepository.findByUserIdAndEndTimeIsNull(userId)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    // Get total play time for a user (in seconds)
    public Long getTotalPlayTime(UUID userId) {
        return sessionRepository.sumDurationByUserId(userId);
    }

    // Get daily active users count for a given date
    public Long getDailyActiveUsers(LocalDateTime date) {
        LocalDateTime start = date.toLocalDate().atStartOfDay();
        LocalDateTime end = start.plusDays(1);
        return sessionRepository.countDistinctUsersByDateRange(start, end);
    }

    // Get total sessions count for a user
    public long getSessionCountByUser(UUID userId) {
        return sessionRepository.countByUserId(userId);
    }

    // Mapper
    private GameSessionDTO toDTO(GameSession session) {
        return new GameSessionDTO(
                session.getId(),
                session.getStartTime(),
                session.getEndTime(),
                session.getDuration(),
                session.getUser().getId(),
                session.getGame().getId());
    }
}