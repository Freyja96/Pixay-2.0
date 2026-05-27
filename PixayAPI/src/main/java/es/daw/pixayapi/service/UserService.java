package es.daw.pixayapi.service;

import es.daw.pixayapi.dto.UserProfileDto;
import es.daw.pixayapi.entity.User;
import es.daw.pixayapi.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder; // Inyectas el Bean de SecurityConfig
    private final FollowService followService;


    // ============ CONVERSION ============
    private UserProfileDto toDTO(User user) {
        UserProfileDto dto = new UserProfileDto();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setDescription(user.getDescription());
        dto.setProfilePicture(user.getProfilePicture());
        dto.setAllowDownloads(user.getAllowDownloads());
        dto.setCommentsPrivacy(user.getCommentsPrivacy());
        dto.setImageVisibility(user.getImageVisibility());
        dto.setPublicProfile(user.isPublicProfile());
        dto.setRole(user.getRole().getName());
        return dto;
    }


    public Long getUserId(UserDetails userDetails) {
        return getUser(userDetails).getId();
    }

    public User getUser(UserDetails userDetails) {
        return userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    }

    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + id));
    }

    public User findByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado: " + username));
    }

    public void registrarUsuario(User user) {
        //Recibe contraseña plana
        String passwordPlana = user.getPassword();
        // hashea
        String passwordHasheada = passwordEncoder.encode(passwordPlana);
        //Guarda el hash en la BD
        user.setPassword(passwordHasheada);
        userRepository.save(user);
    }

    public UserProfileDto getMyProfile(UserDetails userDetails) {
        return toDTO(getUser(userDetails));
    }

    public void save(User user) {
        userRepository.save(user);
    }

    public boolean canViewProfile(User target, UserDetails requesterDetails) {
        User requester = resolveUser(requesterDetails);
        if (target.isPublicProfile()) return true;
        if (requester == null) return false;
        if (isSameUser(requester, target) || isAdmin(requester)) return true;
        return followService.existsFollow(requester.getId(), target.getId());
    }

    public boolean canViewImagesOf(User target, UserDetails requesterDetails) {
        User requester = resolveUser(requesterDetails);
        if (!canViewProfile(target, requesterDetails)) return false;

        String visibility = target.getImageVisibility() == null ? "PUBLIC" : target.getImageVisibility();
        if ("PUBLIC".equalsIgnoreCase(visibility)) {
            return true;
        }

        return requester != null
                && (isSameUser(requester, target)
                || isAdmin(requester)
                || followService.existsFollow(requester.getId(), target.getId()));
    }

    public boolean canCommentOn(User target, UserDetails requesterDetails) {
        User requester = resolveUser(requesterDetails);
        if (requester == null) return false;

        String privacy = target.getCommentsPrivacy();
        switch (privacy) {
            case "PUBLIC":
                return true;
            case "FRIENDS":
                return isSameUser(requester, target)
                        || followService.existsFollow(requester.getId(), target.getId());
            case "PRIVATE":
                return isSameUser(requester, target) || isAdmin(requester);
            default:
                return false;
        }
    }

    private boolean isSameUser(User a, User b) {
        return a.getId().equals(b.getId());
    }

    private boolean isAdmin(User user) {
        return user.getRole() != null && "ROLE_ADMIN".equalsIgnoreCase(user.getRole().getName());
    }

    private User resolveUser(UserDetails userDetails) {
        if (userDetails == null) return null;
        return userRepository.findByUsername(userDetails.getUsername()).orElse(null);
    }
}