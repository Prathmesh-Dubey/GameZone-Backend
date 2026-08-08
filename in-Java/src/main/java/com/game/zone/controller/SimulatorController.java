package com.game.zone.controller;

import com.game.zone.dto.SimulatorDTO;
import com.game.zone.model.Simulator;
import com.game.zone.service.SimulatorService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/simulators")
@CrossOrigin(origins = "*")
public class SimulatorController {

    private final SimulatorService simulatorService;

    public SimulatorController(SimulatorService simulatorService) {
        this.simulatorService = simulatorService;
    }

    @GetMapping
    public ResponseEntity<List<SimulatorDTO>> getAllSimulators() {
        return ResponseEntity.ok(simulatorService.getAllSimulators());
    }

    @GetMapping("/active")
    public ResponseEntity<List<SimulatorDTO>> getActiveSimulators() {
        return ResponseEntity.ok(simulatorService.getActiveSimulators());
    }

    @GetMapping("/{id}")
    public ResponseEntity<SimulatorDTO> getSimulatorById(@PathVariable UUID id) {
        return ResponseEntity.ok(simulatorService.getSimulatorById(id));
    }

    @GetMapping("/{id}/code")
    public ResponseEntity<String> getSimulatorCode(@PathVariable UUID id) {
        Simulator simulator = simulatorService.getSimulatorEntity(id);
        return ResponseEntity.ok(simulator.getSimulatorCode() != null ? simulator.getSimulatorCode() : "");
    }

    @PostMapping
    public ResponseEntity<SimulatorDTO> createSimulator(@RequestBody SimulatorDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(simulatorService.createSimulator(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<SimulatorDTO> updateSimulator(@PathVariable UUID id, @RequestBody SimulatorDTO dto) {
        return ResponseEntity.ok(simulatorService.updateSimulator(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSimulator(@PathVariable UUID id) {
        simulatorService.deleteSimulator(id);
        return ResponseEntity.noContent().build();
    }
}
