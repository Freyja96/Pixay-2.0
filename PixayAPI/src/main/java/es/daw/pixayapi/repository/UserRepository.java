package es.daw.pixayapi.repository;

import es.daw.pixayapi.entity.User;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findById(Long id);

    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);


    @Modifying
    @Transactional
    @Query(value = "INSERT INTO user_follows (follower_id, following_id) VALUES (:me, :them)", nativeQuery = true)
    void follow(@Param("me") Long followerId, @Param("them") Long followingId);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM user_follows WHERE follower_id = :me AND following_id = :them", nativeQuery = true)
    void unfollow(@Param("me") Long followerId, @Param("them") Long followingId);

    @Query(value = "SELECT COUNT(*) FROM user_follows WHERE following_id = :id", nativeQuery = true)
    int countFollowers(@Param("id") Long userId);

    @Query(value = "SELECT COUNT(*) FROM user_follows WHERE follower_id = :id", nativeQuery = true)
    int countFollowing(@Param("id") Long userId);

    @Query(value = "SELECT COUNT(*) > 0 FROM user_follows WHERE follower_id = :me AND following_id = :them", nativeQuery = true)
    boolean isFollowing(@Param("me") Long followerId, @Param("them") Long followingId);
}