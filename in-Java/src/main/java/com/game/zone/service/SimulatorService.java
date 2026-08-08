package com.game.zone.service;

import com.game.zone.dto.SimulatorDTO;
import com.game.zone.model.Simulator;
import com.game.zone.repository.SimulatorRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class SimulatorService {

    private final SimulatorRepository simulatorRepository;

    public SimulatorService(SimulatorRepository simulatorRepository) {
        this.simulatorRepository = simulatorRepository;
    }

    public List<SimulatorDTO> getAllSimulators() {
        return simulatorRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public List<SimulatorDTO> getActiveSimulators() {
        return simulatorRepository.findByActiveTrue().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public SimulatorDTO getSimulatorById(UUID id) {
        Simulator simulator = simulatorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Simulator not found"));
        return toDTO(simulator);
    }

    public Simulator getSimulatorEntity(UUID id) {
        return simulatorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Simulator not found"));
    }

    @Transactional
    public SimulatorDTO createSimulator(SimulatorDTO dto) {
        Simulator simulator = new Simulator();
        simulator.setTitle(dto.getTitle());
        simulator.setDescription(dto.getDescription());
        simulator.setCategory(dto.getCategory());
        simulator.setThumbnail(dto.getThumbnail());
        simulator.setActive(dto.getActive() != null ? dto.getActive() : true);
        simulator.setSimulatorCode(dto.getSimulatorCode());
        simulator.setDynamic(dto.getIsDynamic() != null ? dto.getIsDynamic() : true);
        simulator.setType(dto.getType() != null && !dto.getType().isBlank() ? dto.getType() : "simulator");
        Simulator saved = simulatorRepository.save(simulator);
        return toDTO(saved);
    }

    @Transactional
    public SimulatorDTO updateSimulator(UUID id, SimulatorDTO dto) {
        Simulator simulator = simulatorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Simulator not found"));
        if (dto.getTitle() != null)
            simulator.setTitle(dto.getTitle());
        if (dto.getDescription() != null)
            simulator.setDescription(dto.getDescription());
        if (dto.getCategory() != null)
            simulator.setCategory(dto.getCategory());
        if (dto.getThumbnail() != null)
            simulator.setThumbnail(dto.getThumbnail());
        if (dto.getActive() != null)
            simulator.setActive(dto.getActive());
        if (dto.getSimulatorCode() != null)
            simulator.setSimulatorCode(dto.getSimulatorCode());
        if (dto.getIsDynamic() != null)
            simulator.setDynamic(dto.getIsDynamic());
        if (dto.getType() != null)
            simulator.setType(dto.getType());
        Simulator updated = simulatorRepository.save(simulator);
        return toDTO(updated);
    }

    public void deleteSimulator(UUID id) {
        simulatorRepository.deleteById(id);
    }

    private SimulatorDTO toDTO(Simulator simulator) {
        return new SimulatorDTO(
                simulator.getId(),
                simulator.getTitle(),
                simulator.getDescription(),
                simulator.getCategory(),
                simulator.getThumbnail(),
                simulator.isActive(),
                simulator.getSimulatorCode(),
                simulator.isDynamic(),
                simulator.getType(),
                simulator.getCreatedAt(),
                simulator.getUpdatedAt());
    }
}
