package es.daw.pixaymvc.dto;

import lombok.Data;

@Data
public class UserProfileUpdateRequest {
    private String username;
    private String email;
    private String description;
    private String password;
    private byte[] profilePicture;
}
