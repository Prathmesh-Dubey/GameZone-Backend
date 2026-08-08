package com.game.zone.controller;

import com.game.zone.dto.NotificationDTO;
import com.game.zone.dto.NotificationRequestDTO;
import com.game.zone.service.NotificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/notifications")
@CrossOrigin(origins = "*")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping("/active")
    public ResponseEntity<List<NotificationDTO>> getActiveNotifications() {
        return ResponseEntity.ok(notificationService.getActiveNotifications());
    }

    @GetMapping
    public ResponseEntity<List<NotificationDTO>> getAllNotifications() {
        return ResponseEntity.ok(notificationService.getAllNotifications());
    }

    @PostMapping
    public ResponseEntity<NotificationDTO> createNotification(
            @RequestBody NotificationRequestDTO request,
            @RequestParam UUID adminId) {
        return ResponseEntity.ok(notificationService.createNotification(request, adminId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<NotificationDTO> updateNotification(
            @PathVariable UUID id,
            @RequestBody NotificationRequestDTO request) {
        return ResponseEntity.ok(notificationService.updateNotification(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNotification(@PathVariable UUID id) {
        notificationService.deleteNotification(id);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{id}/toggle")
    public ResponseEntity<NotificationDTO> toggleActive(@PathVariable UUID id) {
        return ResponseEntity.ok(notificationService.toggleActive(id));
    }
}
