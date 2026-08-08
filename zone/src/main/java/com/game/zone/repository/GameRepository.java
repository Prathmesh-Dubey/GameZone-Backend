package com.game.zone.repository;

import com.game.zone.model.Game;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface GameRepository extends JpaRepository<Game, UUID> {
    List<Game> findByActiveTrue(); // only active games

    List<Game> findByCategory(String category);

    List<Game> findByType(String type);

    List<Game> findByActiveTrueAndType(String type);
}