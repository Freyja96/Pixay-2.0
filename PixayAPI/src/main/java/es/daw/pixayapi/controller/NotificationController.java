package es.daw.pixayapi.controller;

import es.daw.pixayapi.dto.response.NotificationResponse;
import es.daw.pixayapi.entity.User;
import es.daw.pixayapi.service.NotificationService;
import es.daw.pixayapi.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/notificaciones")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;
    private final UserService userService;

    @GetMapping("/me")
    public ResponseEntity<List<NotificationResponse>> myNotifications(@AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) return ResponseEntity.status(401).build();
        User user = userService.findByUsername(userDetails.getUsername());
        return ResponseEntity.ok(notificationService.getByUser(user.getId()));
    }

    @GetMapping("/me/no-leidas/count")
    public ResponseEntity<Map<String, Long>> unreadCount(@AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) return ResponseEntity.status(401).build();
        User user = userService.findByUsername(userDetails.getUsername());
        return ResponseEntity.ok(Map.of("count", notificationService.countUnread(user.getId())));
    }

    @PatchMapping("/{id}/leer")
    public ResponseEntity<Void> markAsRead(@PathVariable Long id,
                                           @AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) return ResponseEntity.status(401).build();
        User user = userService.findByUsername(userDetails.getUsername());
        boolean ok = notificationService.markAsRead(user.getId(), id);
        return ok ? ResponseEntity.noContent().build() : ResponseEntity.status(403).build();
    }

    @PatchMapping("/me/leer-todas")
    public ResponseEntity<Void> markAllAsRead(@AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) return ResponseEntity.status(401).build();
        User user = userService.findByUsername(userDetails.getUsername());
        notificationService.markAllAsRead(user.getId());
        return ResponseEntity.noContent().build();
    }
}
