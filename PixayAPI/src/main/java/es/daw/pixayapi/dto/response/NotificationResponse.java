package es.daw.pixayapi.dto.response;

import java.time.LocalDateTime;

public record NotificationResponse(
        Long id,
        String type,
        String message,
        boolean isRead,
        LocalDateTime createdAt
) {
}
