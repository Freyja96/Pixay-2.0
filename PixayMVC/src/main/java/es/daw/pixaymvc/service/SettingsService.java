package es.daw.pixaymvc.service;

import es.daw.pixaymvc.dto.SettingsViewModel;
import es.daw.pixaymvc.dto.response.UserProfileResponse;
import org.springframework.stereotype.Service;

@Service
public interface SettingsService {

    SettingsViewModel getSettings(Long userId, String token);


    void actualizarVisibilidad(boolean perfilPrivado, String token);


    void actualizarPermisos(String campo, boolean valor, String token);


    UserProfileResponse getUserSummary(Long usuarioId, String token);
}
