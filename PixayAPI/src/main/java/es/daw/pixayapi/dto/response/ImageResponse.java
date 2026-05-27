package es.daw.pixayapi.dto.response;

public record ImageResponse (
    Long id,
    String title,
    byte[] content,
    Long userId,
    String category,
    String subcategory
) {}