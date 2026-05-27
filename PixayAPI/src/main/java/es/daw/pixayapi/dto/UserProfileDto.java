package es.daw.pixayapi.dto;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class UserProfileDto {
    private Long id;
    private String username;
    private String email;
    private String description;
    private byte[] profilePicture;
    private Boolean allowDownloads;
    private String commentsPrivacy;
    private String imageVisibility;
    private Boolean publicProfile;
    private String role;

    private long followersCount;
    private long followingCount;
}

