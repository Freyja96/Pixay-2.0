package es.daw.pixaymvc.dto.response;

public record ImageResponse(
        Long id,
        String title,
        byte[] content,
        Long userId,
        String categoryName,
        String subcategoryName
) {
    public String getContentBase64() {
        return content != null ? java.util.Base64.getEncoder().encodeToString(content) : "";
    }
}