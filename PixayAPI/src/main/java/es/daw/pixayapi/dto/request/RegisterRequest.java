package es.daw.pixayapi.dto.request;


import jakarta.validation.constraints.*;

public record RegisterRequest(
        @NotBlank(message = "El nombre de usuario es obligatorio")
        @Size(min = 3, max = 20, message = "El usuario debe tener entre 3 y 20 caracteres")
        String username,

        @NotBlank(message = "El email es obligatorio")
        @Email(message = "El formato del email no es válido")
        String email,

        @NotBlank(message = "La contraseña es obligatoria")
        @Size(min = 8, message = "La contraseña debe tener al menos 8 caracteres")
        @Pattern(
                regexp = "^(?=.*[0-9])(?=.*[A-Z])(?=.*[@#$%^&+=!._-]).*$",
                message = "La contraseña debe tener una mayúscula, un número y un carácter especial"
        )
        String password
) {}