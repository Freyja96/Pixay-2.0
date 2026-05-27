package es.daw.pixaymvc.dto.response;

import java.time.LocalDateTime;

public record CommentResponse(
        Long userId,
        String username,
        String avatar,
        String content,
        LocalDateTime timestamp
) {}