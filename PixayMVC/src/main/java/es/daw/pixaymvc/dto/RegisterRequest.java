package es.daw.pixaymvc.dto;

import jakarta.validation.constraints.*;

public record RegisterRequest(
        @NotBlank(message = "El nombre de usuario no puede estar vacío")
        @Size(min = 3, max = 20, message = "El usuario debe tener entre 3 y 20 caracteres")
        String username,

        @NotBlank(message = "El email es obligatorio")
        @Email(message = "Email no válido")
        String email,

        @NotBlank(message = "La contraseña es obligatoria")
        @Size(min = 8, message = "La contraseña debe tener mínimo 8 caracteres")
        @Pattern(
                regexp = "^(?=.*[0-9])(?=.*[A-Z])(?=.*[@#$%^&+=!._-]).*$",
                message = "La contraseña debe contener al menos una mayúscula, un número y un carácter especial"
        )
        String password
) {}