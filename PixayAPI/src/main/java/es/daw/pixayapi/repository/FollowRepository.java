package es.daw.pixayapi.repository;

import es.daw.pixayapi.entity.Follow;
import es.daw.pixayapi.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface FollowRepository extends JpaRepository<Follow, Long> {

    boolean existsByFollowerAndFollowed(User follower, User followed);

    Optional<Follow> findByFollowerAndFollowed(User follower, User followed);

    long countByFollowed(User followed);

    long countByFollower(User follower);

    List<Follow> findByFollowed(User followed);

    List<Follow> findByFollower(User follower);

    @Query("SELECT CASE WHEN COUNT(f) > 0 THEN true ELSE false END " +
            "FROM Follow f WHERE f.follower.id = :followerId AND f.followed.id = :followedId")
    boolean existsByFollowerIdAndFollowedId(@Param("followerId") Long followerId,
                                            @Param("followedId") Long followedId);
}
