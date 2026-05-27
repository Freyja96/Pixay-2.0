package es.daw.pixayapi.dto.request;


import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserProfileUpdateRequest {
    private String username;
    private String email;

    @Size(max = 500, message = "La descripción no puede tener más de 500 caracteres")
    private String description;

    private String password;

    private byte[] profilePicture;

    private Boolean allowDownloads;

    @Pattern(regexp = "PUBLIC|FRIENDS|PRIVATE", message = "Comentarios solo pueden ser PUBLIC, FRIENDS o PRIVATE")
    private String commentsPrivacy;

    @Pattern(regexp = "PUBLIC|FOLLOWERS", message = "Visibilidad de imágenes solo puede ser PUBLIC o FOLLOWERS")
    private String imageVisibility;

    private Boolean publicProfile;
}
