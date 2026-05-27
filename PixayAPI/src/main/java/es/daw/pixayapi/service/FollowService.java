package es.daw.pixayapi.service;

import es.daw.pixayapi.entity.Follow;
import es.daw.pixayapi.entity.User;
import es.daw.pixayapi.repository.FollowRepository;
import es.daw.pixayapi.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class FollowService {

    @Autowired
    private FollowRepository followRepository;

    @Autowired
    private UserRepository userRepository;

    // Logica para seguir a un usuario --------------------------------
    @Transactional
    public void follow(Long followerId, Long followedId) {

        // Evitar seguir a ti mismo
        if (followerId.equals(followedId)) {
            throw new IllegalArgumentException("No puedes seguirte a ti mismo");
        }
        // Encotrar Id de seguidor y seguido
        User follower = userRepository.findById(followerId).orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + followerId));
        User followed = userRepository.findById(followedId).orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + followedId));

        // Evtar duplicados
        if (!followRepository.existsByFollowerAndFollowed(follower, followed)) {
            Follow follow = new Follow(follower, followed);
            followRepository.save(follow);
        }
    }

    @Transactional
    public void unfollow(Long followerId, Long followedId) {
        // Encotrar Id de seguidor y seguido
        User follower = userRepository.findById(followerId).orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + followerId));
        User followed = userRepository.findById(followedId).orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + followedId));

        //Mirar si ya se siguen y borrarlo
        followRepository.findByFollowerAndFollowed(follower, followed)
                .ifPresent(followRepository::delete);

    }

    public long countFollowers(Long followedId) {
        User user = userRepository.findById(followedId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + followedId));
        return followRepository.countByFollowed(user);
    }

    public  long countFollowing(Long followerId) {
        User user = userRepository.findById(followerId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + followerId));
        return followRepository.countByFollower(user);
    }

    // Mirar si usuario 1 sigue a usuario 2
    public boolean existsFollow(Long followerId, Long followedId) {
        return followRepository.existsByFollowerIdAndFollowedId(followerId, followedId);
    }

    public boolean puedeVerPerfil(Long viewerId, Long profielOwnerId) {
        User owner = userRepository.findById(profielOwnerId).orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + profielOwnerId));

        // Si es público, cualquiera ve
        if (owner.isPublicProfile()){
            return true;
        }
        if (viewerId == null) {
            return false;
        }
        // Si es privado, solo sus seguidores (o él mismo) ven
        if (viewerId.equals(owner.getId())) {
            return true;
        }

        return existsFollow(viewerId, profielOwnerId);
    }


}
