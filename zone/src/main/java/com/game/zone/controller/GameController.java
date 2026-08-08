package com.game.zone.controller;

import com.game.zone.dto.GameDTO;
import com.game.zone.model.Game;
import com.game.zone.service.GameService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/games")
@CrossOrigin(origins = "*")

public class GameController {

    private final GameService gameService;

    public GameController(GameService gameService) {
        this.gameService = gameService;
    }

    @GetMapping
    public ResponseEntity<List<GameDTO>> getAllGames(@RequestParam(required = false) String type) {
        if (type != null && !type.isBlank()) {
            return ResponseEntity.ok(gameService.getGamesByType(type));
        }
        return ResponseEntity.ok(gameService.getAllGames());
    }

    @GetMapping("/simulators")
    public ResponseEntity<List<GameDTO>> getSimulators() {
        return ResponseEntity.ok(gameService.getGamesByType("simulator"));
    }

    @GetMapping("/{id}/code")
    public ResponseEntity<String> getGameCode(@PathVariable UUID id) {
        Game game = gameService.getGameEntity(id);
        return ResponseEntity.ok(game.getGameCode() != null ? game.getGameCode() : "");
    }

    @GetMapping("/active")
    public ResponseEntity<List<GameDTO>> getActiveGames(@RequestParam(required = false) String type) {
        if (type != null && !type.isBlank()) {
            return ResponseEntity.ok(gameService.getActiveGamesByType(type));
        }
        return ResponseEntity.ok(gameService.getAllGames().stream()
                .filter(GameDTO::getActive)
                .collect(Collectors.toList()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<GameDTO> getGameById(@PathVariable UUID id) {
        return ResponseEntity.ok(gameService.getGameById(id));
    }

    @PostMapping
    public ResponseEntity<GameDTO> createGame(@RequestBody GameDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(gameService.createGame(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<GameDTO> updateGame(@PathVariable UUID id, @RequestBody GameDTO dto) {
        return ResponseEntity.ok(gameService.updateGame(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGame(@PathVariable UUID id) {
        gameService.deleteGame(id);
        return ResponseEntity.noContent().build();
    }
}