package es.daw.pixayapi.repository;

import es.daw.pixayapi.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByImageIdOrderByCreatedAtAsc(Long imageId);
}