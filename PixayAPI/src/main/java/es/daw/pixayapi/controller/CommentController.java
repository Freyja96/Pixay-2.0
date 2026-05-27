package es.daw.pixayapi.controller;

import es.daw.pixayapi.dto.response.CommentResponse;
import es.daw.pixayapi.entity.Comment;
import es.daw.pixayapi.entity.User;
import es.daw.pixayapi.repository.CommentRepository;
import es.daw.pixayapi.repository.ImageRepository;
import es.daw.pixayapi.service.NotificationService;
import es.daw.pixayapi.service.UserService;
import org.springframework.security.core.userdetails.UserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/imagenes")
@RequiredArgsConstructor
public class CommentController {
    private final CommentRepository commentRepository;
    private final ImageRepository imageRepository;
    private final NotificationService notificationService;
    private final UserService userService;

    @GetMapping("/{imageId}/comentarios")
    public ResponseEntity<List<CommentResponse>> getComments(@PathVariable Long imageId) {
        List<Comment> comments = commentRepository.findByImageIdOrderByCreatedAtAsc(imageId);
        List<CommentResponse> response = comments.stream()
                .map(c -> new CommentResponse(
                        c.getUser().getId(),
                        c.getUser().getUsername(),
                        c.getUser().getProfilePicture() != null ? "/api/usuarios/" + c.getUser().getId() + "/avatar" : "",
                        c.getContent(),
                        c.getCreatedAt()
                )).toList();
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{imageId}/comentarios")
    public ResponseEntity<?> saveComment(@PathVariable Long imageId, @RequestBody Map<String, String> body, @AuthenticationPrincipal UserDetails userDetails) {
        User user = userService.findByUsername(userDetails.getUsername());
        Comment comment = new Comment();
        comment.setContent(body.get("content"));
        comment.setImageId(imageId);
        comment.setUser(user);
        commentRepository.save(comment);
        return ResponseEntity.ok().build();
    }
}