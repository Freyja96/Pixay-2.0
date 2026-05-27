package es.daw.pixayapi.repository;

import es.daw.pixayapi.entity.Image;
import es.daw.pixayapi.entity.ImageReaction;
import es.daw.pixayapi.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ImageReactionRepository extends JpaRepository<ImageReaction, Long> {
    Optional<ImageReaction> findByUserAndImage(User user, Image image);

    @Query("SELECT r.reactionType, COUNT(r) FROM ImageReaction r WHERE r.image.id = :imageId GROUP BY r.reactionType")
    List<Object[]> countReactionsByImageId(@Param("imageId") Long imageId);
}