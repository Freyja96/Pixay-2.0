package es.daw.pixayapi.controller;

import es.daw.pixayapi.dto.request.UserProfileUpdateRequest;
import es.daw.pixayapi.dto.response.UserProfileResponse;
import es.daw.pixayapi.entity.User;
import es.daw.pixayapi.repository.UserRepository;
import es.daw.pixayapi.security.JwtService;
import es.daw.pixayapi.service.FollowService;
import es.daw.pixayapi.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;
    private final UserRepository userRepository;
    private final FollowService followService;

    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserProfileResponse> getMyProfile(@AuthenticationPrincipal UserDetails userDetails) {
        User user = userService.findByUsername(userDetails.getUsername());

        UserProfileResponse response = new UserProfileResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getProfilePicture(),
                user.getDescription(),
                followService.countFollowers(user.getId()),
                followService.countFollowing(user.getId()),
                user.isPublicProfile(),
                user.getCommentsPrivacy(),
                Boolean.TRUE.equals(user.getAllowDownloads()),
                user.getImageVisibility()
        );
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserProfileResponse> getUserProfile(@AuthenticationPrincipal UserDetails userDetails,
                                                              @PathVariable Long id) {
        User user = userService.findById(id);

        Long viewerId = (userDetails != null) ? userService.getUserId(userDetails) : null;

        boolean puedeVer = followService.puedeVerPerfil(viewerId, user.getId());

        UserProfileResponse response;
        if (puedeVer) {
            response = new UserProfileResponse(
                    user.getId(),
                    user.getUsername(),
                    user.getEmail(),
                    user.getProfilePicture(),
                    user.getDescription(),
                    followService.countFollowers(id),
                    followService.countFollowing(id),
                    user.isPublicProfile(),
                    user.getCommentsPrivacy(),
                    Boolean.TRUE.equals(user.getAllowDownloads()),
                    user.getImageVisibility()
            );
        } else {
            response = new UserProfileResponse(
                    user.getId(),
                    user.getUsername(),
                    null,
                    user.getProfilePicture(),
                    "Este usuario es privado. Síguelo para ver su contenido.",
                    followService.countFollowers(id),
                    followService.countFollowing(id),
                    user.isPublicProfile(),
                    user.getCommentsPrivacy(),
                    Boolean.TRUE.equals(user.getAllowDownloads()),
                    user.getImageVisibility()

            );

        }

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> updateProfile(@AuthenticationPrincipal UserDetails userDetails,
                                              @RequestBody UserProfileUpdateRequest dto) {
        User user = userService.findByUsername(userDetails.getUsername());

        if (dto.getEmail() != null ) user.setEmail(dto.getEmail());
        if (dto.getDescription() != null ) user.setDescription(dto.getDescription());
        if (dto.getProfilePicture() != null) user.setProfilePicture(dto.getProfilePicture());
        if (dto.getPublicProfile() != null) user.setPublicProfile(dto.getPublicProfile());
        if (dto.getAllowDownloads() != null) user.setAllowDownloads(dto.getAllowDownloads());
        if (dto.getCommentsPrivacy() != null) user.setCommentsPrivacy(dto.getCommentsPrivacy());
        if (dto.getImageVisibility() != null) user.setImageVisibility(dto.getImageVisibility());
        if (dto.getPassword() != null && !dto.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(dto.getPassword()));
        }

        userService.save(user);

        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/seguir")
    public ResponseEntity<?> toggleFollow(@PathVariable Long id, @AuthenticationPrincipal User me) {
        if (me.getId().equals(id)) return ResponseEntity.badRequest().body("No puedes seguirte a ti mismo");

        if (userRepository.isFollowing(me.getId(), id)) {
            userRepository.unfollow(me.getId(), id);
        } else {
            userRepository.follow(me.getId(), id);
        }
        return ResponseEntity.ok().build();
    }
}