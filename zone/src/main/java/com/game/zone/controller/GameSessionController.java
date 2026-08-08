package com.game.zone.controller;

import com.game.zone.dto.GameSessionDTO;
import com.game.zone.service.GameSessionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/sessions")
@CrossOrigin(origins = "*")
public class GameSessionController {

    private GameSessionService sessionService;

    public GameSessionController(GameSessionService sessionService) {
        this.sessionService = sessionService;
    }

    @PostMapping("/start")
    public ResponseEntity<GameSessionDTO> startSession(
            @RequestParam UUID userId,
            @RequestParam UUID gameId) {

        GameSessionDTO session = sessionService.startSession(userId, gameId);
        return ResponseEntity.status(HttpStatus.CREATED).body(session);
    }

    // PUT /api/sessions/end/{sessionId}
    @PutMapping("/end/{sessionId}")
    public ResponseEntity<GameSessionDTO> endSession(@PathVariable UUID sessionId) {
        GameSessionDTO session = sessionService.endSession(sessionId);
        return ResponseEntity.ok(session);
    }

    // GET /api/sessions/user/{userId}
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<GameSessionDTO>> getSessionsByUser(@PathVariable UUID userId) {
        return ResponseEntity.ok(sessionService.getSessionsByUser(userId));
    }

    // GET /api/sessions/game/{gameId}
    @GetMapping("/game/{gameId}")
    public ResponseEntity<List<GameSessionDTO>> getSessionsByGame(@PathVariable UUID gameId) {
        return ResponseEntity.ok(sessionService.getSessionsByGame(gameId));
    }

    // GET /api/sessions/active/user/{userId}
    @GetMapping("/active/user/{userId}")
    public ResponseEntity<List<GameSessionDTO>> getActiveSessions(@PathVariable UUID userId) {
        return ResponseEntity.ok(sessionService.getActiveSessionsByUser(userId));
    }

    // GET /api/sessions/total-time/user/{userId}
    @GetMapping("/total-time/user/{userId}")
    public ResponseEntity<Long> getTotalPlayTime(@PathVariable UUID userId) {
        return ResponseEntity.ok(sessionService.getTotalPlayTime(userId));
    }

    // GET /api/sessions/dau?date=2026-07-18T00:00:00
    @GetMapping("/dau")
    public ResponseEntity<Long> getDailyActiveUsers(@RequestParam LocalDateTime date) {
        return ResponseEntity.ok(sessionService.getDailyActiveUsers(date));
    }

    // GET /api/sessions/count/user/{userId}
    @GetMapping("/count/user/{userId}")
    public ResponseEntity<Long> getSessionCount(@PathVariable UUID userId) {
        return ResponseEntity.ok(sessionService.getSessionCountByUser(userId));
    }
}