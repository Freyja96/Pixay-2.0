package es.daw.pixaymvc.service;

import es.daw.pixaymvc.dto.SettingsViewModel;
import es.daw.pixaymvc.dto.response.UserProfileResponse;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

@Service
public class SettingsServiceImpl implements SettingsService {

    private final WebClient webClientAPI;

    public SettingsServiceImpl(WebClient webClientAPI) {
        this.webClientAPI = webClientAPI;
    }

    @Override
    public SettingsViewModel getSettings(Long userId, String token) {
        UserProfileResponse user = getUserSummary(userId, token);

        SettingsViewModel vm = new SettingsViewModel();
        vm.setPerfilPrivado(!user.publicProfile());
        vm.setComentariosHabilitados(!"PRIVATE".equalsIgnoreCase(user.commentsPrivacy()));
        vm.setPermitirDescargas(user.allowDownloads());
        vm.setVisibilidadImagenes(user.imageVisibility());
        return vm;
    }

    @Override
    public void actualizarVisibilidad(boolean privateProfile, String token) {
        webClientAPI.patch()
                .uri("ajustes/me/visibilidad")
                .headers(h -> h.setBearerAuth(token))
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(Map.of("publicProfile", !privateProfile))
                .retrieve()
                .toBodilessEntity()
                .block();
    }

    @Override
    public void actualizarPermisos(String campo, boolean valor, String token) {
        Map<String, Object> payload = switch (campo) {
            case "comentariosHabilitados" -> Map.of("commentsPrivacy", valor ? "PUBLIC" : "PRIVATE");
            case "permitirDescargas" -> Map.of("allowDownloads", valor);
            case "visibilidadImagenes" -> Map.of("imageVisibility", valor ? "FOLLOWERS" : "PUBLIC");
            default -> throw new IllegalArgumentException("Campo de ajuste no soportado: " + campo);
        };

        webClientAPI.patch()
                .uri("ajustes/me/permisos")
                .headers(h -> h.setBearerAuth(token))
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(payload)
                .retrieve()
                .toBodilessEntity()
                .block();
    }

    @Override
    public UserProfileResponse getUserSummary(Long usuarioId, String token) {
        return webClientAPI.get()
                .uri("usuarios/me")
                .headers(h -> h.setBearerAuth(token))
                .retrieve()
                .bodyToMono(UserProfileResponse.class)
                .block();
    }


}
