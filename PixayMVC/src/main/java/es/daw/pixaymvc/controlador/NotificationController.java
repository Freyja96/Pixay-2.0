package es.daw.pixaymvc.controlador;

import es.daw.pixaymvc.dto.response.NotificationResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/notificaciones")
@RequiredArgsConstructor
public class NotificationController {

    private final WebClient webClientAPI;

    @GetMapping
    public ResponseEntity<List<NotificationResponse>> getMyNotifications(HttpSession session) {
        String token = (String) session.getAttribute("token");
        if (token == null) return ResponseEntity.status(401).build();

        List<NotificationResponse> response = webClientAPI.get()
                .uri("notificaciones/me")
                .headers(h -> h.setBearerAuth(token))
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<NotificationResponse>>() {
                })
                .block();
        return ResponseEntity.ok(response == null ? List.of() : response);
    }

    @GetMapping("/no-leidas/count")
    public ResponseEntity<Map<String, Long>> getUnreadCount(HttpSession session) {
        String token = (String) session.getAttribute("token");
        if (token == null) return ResponseEntity.ok(Map.of("count", 0L));

        Map<String, Long> response = webClientAPI.get()
                .uri("notificaciones/me/no-leidas/count")
                .headers(h -> h.setBearerAuth(token))
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Map<String, Long>>() {
                })
                .block();
        return ResponseEntity.ok(response == null ? Map.of("count", 0L) : response);
    }

    @PatchMapping("/{id}/leer")
    public ResponseEntity<Void> markAsRead(@PathVariable Long id, HttpSession session) {
        String token = (String) session.getAttribute("token");
        if (token == null) return ResponseEntity.status(401).build();

        webClientAPI.patch()
                .uri("notificaciones/" + id + "/leer")
                .headers(h -> h.setBearerAuth(token))
                .contentType(MediaType.APPLICATION_JSON)
                .retrieve()
                .toBodilessEntity()
                .block();
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/leer-todas")
    public ResponseEntity<Void> markAllAsRead(HttpSession session) {
        String token = (String) session.getAttribute("token");
        if (token == null) return ResponseEntity.status(401).build();

        webClientAPI.patch()
                .uri("notificaciones/me/leer-todas")
                .headers(h -> h.setBearerAuth(token))
                .contentType(MediaType.APPLICATION_JSON)
                .retrieve()
                .toBodilessEntity()
                .block();
        return ResponseEntity.noContent().build();
    }
}
