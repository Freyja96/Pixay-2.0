package es.daw.pixayapi.service;


import es.daw.pixayapi.dto.response.NotificationResponse;
import es.daw.pixayapi.entity.Notification;
import es.daw.pixayapi.entity.User;
import es.daw.pixayapi.repository.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class NotificationService {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private NotificationRepository notificationRepository;

    public void notifyNewMessage(User receiver, User sender, Long imageId) {
        Notification notification = new Notification();
        notification.setUser(receiver);
        notification.setType("NEW_MESSAGE");
        notification.setMessage("Acabas de recibir un nuevo mensaje de " + sender.getUsername());
        Notification saved = notificationRepository.save(notification);

        Map<String, Object> payload = Map.of(
                "id", saved.getId(),
                "type", "NEW_MESSAGE",
                "message", saved.getMessage(),
                "isRead", false,
                "imageId", imageId,
                "createdAt", saved.getCreatedAt().toString()

        );
        // Topic por userId para no depender de auth WebSocket.
        messagingTemplate.convertAndSend("/topic/notifications/" + receiver.getId(), (Object) payload);
    }

    public List<NotificationResponse> getByUser(Long userId) {
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId).stream()
                .map(n -> new NotificationResponse(
                        n.getId(),
                        n.getType(),
                        n.getMessage(),
                        n.isRead(),
                        n.getCreatedAt()
                ))
                .toList();
    }

    public long countUnread(Long userId) {
        return notificationRepository.countByUserIdAndReadFalse(userId);
    }

    public boolean markAsRead(Long userId, Long notificationId) {
        Notification n = notificationRepository.findById(notificationId).orElse(null);
        if (n == null || !n.getUser().getId().equals(userId)) {
            return false;
        }
        if (!n.isRead()) {
            n.setRead(true);
            notificationRepository.save(n);
        }
        return true;
    }

    public void markAllAsRead(Long userId) {
        List<Notification> notifications = notificationRepository.findByUserIdOrderByCreatedAtDesc(userId);
        boolean changed = false;
        for (Notification n : notifications) {
            if (!n.isRead()) {
                n.setRead(true);
                changed = true;
            }
        }
        if (changed) {
            notificationRepository.saveAll(notifications);
        }
    }
}
