package com.game.zone.service;

import com.game.zone.dto.GameDTO;
import com.game.zone.model.Game;
import com.game.zone.repository.GameRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class GameService {

    private final GameRepository gameRepository;

    public GameService(GameRepository gameRepository) {
        this.gameRepository = gameRepository;
    }

    public List<GameDTO> getAllGames() {
        return gameRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public GameDTO getGameById(UUID id) {
        Game game = gameRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Game not found"));
        return toDTO(game);
    }

    // 🔥 New method to get raw Game entity (used for code endpoint)
    public Game getGameEntity(UUID id) {
        return gameRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Game not found"));
    }

    public List<GameDTO> getGamesByType(String type) {
        return gameRepository.findByType(type).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public List<GameDTO> getActiveGamesByType(String type) {
        return gameRepository.findByActiveTrueAndType(type).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public GameDTO createGame(GameDTO dto) {
        Game game = new Game();
        game.setTitle(dto.getTitle());
        game.setDescription(dto.getDescription());
        game.setCategory(dto.getCategory());
        game.setThumbnail(dto.getThumbnail());
        game.setActive(dto.getActive() != null ? dto.getActive() : true);
        // 🔥 Set new fields
        game.setGameCode(dto.getGameCode());
        game.setDynamic(dto.getIsDynamic() != null ? dto.getIsDynamic() : false);
        game.setType(dto.getType() != null && !dto.getType().isBlank() ? dto.getType() : "game");
        Game saved = gameRepository.save(game);
        return toDTO(saved);
    }

    @Transactional
    public GameDTO updateGame(UUID id, GameDTO dto) {
        Game game = gameRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Game not found"));
        if (dto.getTitle() != null)
            game.setTitle(dto.getTitle());
        if (dto.getDescription() != null)
            game.setDescription(dto.getDescription());
        if (dto.getCategory() != null)
            game.setCategory(dto.getCategory());
        if (dto.getThumbnail() != null)
            game.setThumbnail(dto.getThumbnail());
        if (dto.getActive() != null)
            game.setActive(dto.getActive());
        // 🔥 Update new fields if provided
        if (dto.getGameCode() != null)
            game.setGameCode(dto.getGameCode());
        if (dto.getIsDynamic() != null)
            game.setDynamic(dto.getIsDynamic());
        if (dto.getType() != null)
            game.setType(dto.getType());
        Game updated = gameRepository.save(game);
        return toDTO(updated);
    }

    public void deleteGame(UUID id) {
        gameRepository.deleteById(id);
    }

    private GameDTO toDTO(Game game) {
        return new GameDTO(
                game.getId(),
                game.getTitle(),
                game.getDescription(),
                game.getCategory(),
                game.getThumbnail(),
                game.isActive(),
                game.getGameCode(),
                game.isDynamic(),
                game.getType(),
                game.getCreatedAt(),
                game.getUpdatedAt());
    }
}