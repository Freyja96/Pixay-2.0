package es.daw.pixayapi.repository;

import es.daw.pixayapi.entity.Image;
import es.daw.pixayapi.entity.SavedImage;
import es.daw.pixayapi.entity.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface SavedImageRepository extends JpaRepository<SavedImage, Long> {
    Slice<SavedImage> findByUser(User user, Pageable pageable);

    Optional<SavedImage> findByUserAndImage(User user, Image image);
}
