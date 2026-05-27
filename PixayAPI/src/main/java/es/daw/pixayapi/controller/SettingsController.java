package es.daw.pixayapi.controller;


import es.daw.pixayapi.dto.UserProfileDto;
import es.daw.pixayapi.entity.User;
import es.daw.pixayapi.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/api/ajustes")
@RequiredArgsConstructor
public class SettingsController {

    private final UserService userService;

    @GetMapping("/me")
    public ResponseEntity<UserProfileDto> getUserSettings(@AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(userService.getMyProfile(userDetails));
    }
    @PatchMapping("/me/visibilidad")
    public ResponseEntity<Void> updateVisibility(@AuthenticationPrincipal UserDetails userDetails,
                                                 @RequestBody Map<String, Boolean> body) {
        Boolean nuevoEstado = body.get("publicProfile");

        if (nuevoEstado == null || userDetails == null) {
            return ResponseEntity.badRequest().build();
        }

        User user = userService.findByUsername(userDetails.getUsername());
        user.setPublicProfile(nuevoEstado);
        userService.save(user);

        return ResponseEntity.ok().build();
    }

    @PatchMapping("/me/permisos")
    public ResponseEntity<Void> updatePermisos(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody Map<String, Object> body) {
        if (userDetails == null) {
            return ResponseEntity.badRequest().build();
        }
        User user = userService.findByUsername(userDetails.getUsername());

        if (body.containsKey("allowDownloads")) {
            Boolean valor = (Boolean) body.get("allowDownloads");
            user.setAllowDownloads(valor);
        }

        if (body.containsKey("commentsPrivacy")) {
            String valor = (String) body.get("commentsPrivacy");

            if (!List.of("PUBLIC", "FRIENDS", "PRIVATE").contains(valor)) {
                return ResponseEntity.badRequest().build();
            }
            user.setCommentsPrivacy(valor);
        }

        if (body.containsKey("imageVisibility")) {
            String valor = (String) body.get("imageVisibility");
            if (!List.of("PUBLIC", "FOLLOWERS").contains(valor)) {
                return ResponseEntity.badRequest().build();
            }
            user.setImageVisibility(valor);
        }

        userService.save(user);
        return ResponseEntity.ok().build();
    }


}
