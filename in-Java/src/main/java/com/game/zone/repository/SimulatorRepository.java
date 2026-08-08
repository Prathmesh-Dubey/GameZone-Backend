package com.game.zone.repository;

import com.game.zone.model.Simulator;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface SimulatorRepository extends JpaRepository<Simulator, UUID> {
    List<Simulator> findByActiveTrue();

    List<Simulator> findByCategory(String category);
}
