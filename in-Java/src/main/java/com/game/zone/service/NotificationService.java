package com.game.zone.service;

import com.game.zone.dto.NotificationDTO;
import com.game.zone.dto.NotificationRequestDTO;
import com.game.zone.model.Notification;
import com.game.zone.model.User;
import com.game.zone.repository.NotificationRepository;
import com.game.zone.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    public NotificationService(NotificationRepository notificationRepository, UserRepository userRepository) {
        this.notificationRepository = notificationRepository;
        this.userRepository = userRepository;
    }

    public List<NotificationDTO> getActiveNotifications() {
        return notificationRepository.findByActiveTrueOrderByCreatedAtDesc().stream()
                .filter(n -> n.getExpiresAt() == null || n.getExpiresAt().isAfter(LocalDateTime.now()))
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public List<NotificationDTO> getAllNotifications() {
        return notificationRepository.findAllByOrderByCreatedAtDesc().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public NotificationDTO createNotification(NotificationRequestDTO request, UUID adminId) {
        User admin = userRepository.findById(adminId)
                .orElseThrow(() -> new RuntimeException("Admin user not found"));

        if (!"ADMIN".equals(admin.getRole())) {
            throw new RuntimeException("Only admins can create notifications");
        }

        Notification notification = new Notification(
                request.getTitle(),
                request.getMessage(),
                request.getType(),
                admin,
                request.getExpiresAt()
        );

        return mapToDTO(notificationRepository.save(notification));
    }

    public NotificationDTO updateNotification(UUID id, NotificationRequestDTO request) {
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Notification not found"));

        notification.setTitle(request.getTitle());
        notification.setMessage(request.getMessage());
        notification.setType(request.getType());
        notification.setExpiresAt(request.getExpiresAt());

        return mapToDTO(notificationRepository.save(notification));
    }

    public void deleteNotification(UUID id) {
        notificationRepository.deleteById(id);
    }

    public NotificationDTO toggleActive(UUID id) {
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Notification not found"));

        notification.setActive(!notification.isActive());
        return mapToDTO(notificationRepository.save(notification));
    }

    private NotificationDTO mapToDTO(Notification notification) {
        return new NotificationDTO(
                notification.getId(),
                notification.getTitle(),
                notification.getMessage(),
                notification.getType(),
                notification.getCreatedBy().getUsername(),
                notification.getCreatedAt(),
                notification.isActive(),
                notification.getExpiresAt()
        );
    }
}
