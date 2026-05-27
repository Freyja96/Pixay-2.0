package es.daw.pixaymvc.dto.response;


public record UserProfileResponse(
        Long id,
        String username,
        String email,
        byte[] profilePicture,
        String description,
        long followersCount,
        long followingCount,
        boolean publicProfile,
        String commentsPrivacy,
        boolean allowDownloads,
        String imageVisibility
) {
    // pintar foto de perfil
    public String getFotoBase64() {
        return profilePicture != null ? java.util.Base64.getEncoder().encodeToString(profilePicture) : null;
    }

    public String getFotoDataUrl() {
        String base64 = getFotoBase64();
        return base64 != null
                ? "data:image/jpeg;base64," + base64
                : "data:image/gif;base64,R0lGODlhAQABAIAAAAAAAP///ywAAAAAAQABAAACAUwAOw==";
    }
}