package es.daw.pixaymvc.dto;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class SettingsViewModel {

    private boolean perfilPrivado;
    private boolean comentariosHabilitados;
    private boolean permitirDescargas;
    private String visibilidadImagenes;

}
