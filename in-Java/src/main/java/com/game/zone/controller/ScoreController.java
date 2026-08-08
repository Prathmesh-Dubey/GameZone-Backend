package com.game.zone.controller;

import com.game.zone.dto.ScoreDTO;
import com.game.zone.service.ScoreService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/scores")
@CrossOrigin(origins = "*")
public class ScoreController {

    private final ScoreService scoreService;

    public ScoreController(ScoreService scoreService) {
        this.scoreService = scoreService;
    }

    // POST /api/scores - submit a new score
    @PostMapping
    public ResponseEntity<ScoreDTO> submitScore(@RequestBody ScoreDTO dto) {
        ScoreDTO saved = scoreService.saveScore(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    // GET /api/scores/game/{gameId} - all scores for a game
    @GetMapping("/game/{gameId}")
    public ResponseEntity<List<ScoreDTO>> getScoresByGame(@PathVariable UUID gameId) {
        return ResponseEntity.ok(scoreService.getScoresByGame(gameId));
    }

    // GET /api/scores/user/{userId} - all scores by a user
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<ScoreDTO>> getScoresByUser(@PathVariable UUID userId) {
        return ResponseEntity.ok(scoreService.getScoresByUser(userId));
    }

    // GET /api/scores/leaderboard/{gameId} - top 10 scores for a game
    @GetMapping("/leaderboard/{gameId}")
    public ResponseEntity<List<ScoreDTO>> getLeaderboard(@PathVariable UUID gameId) {
        return ResponseEntity.ok(scoreService.getTopScoresByGame(gameId));
    }

    // GET /api/scores/global-leaderboard - top players by total score
    @GetMapping("/global-leaderboard")
    public ResponseEntity<List<com.game.zone.dto.GlobalRankDTO>> getGlobalLeaderboard() {
        return ResponseEntity.ok(scoreService.getGlobalLeaderboard());
    }

    // GET /api/scores/personal-best?userId=...&gameId=...
    @GetMapping("/personal-best")
    public ResponseEntity<ScoreDTO> getPersonalBest(@RequestParam UUID userId,
            @RequestParam UUID gameId) {
        ScoreDTO best = scoreService.getPersonalBest(userId, gameId);
        if (best == null) {
            return ResponseEntity.noContent().build(); // or return 404
        }
        return ResponseEntity.ok(best);
    }
}