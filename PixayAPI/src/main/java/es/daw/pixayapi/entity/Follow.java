package es.daw.pixayapi.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "follows")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Follow {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // The user who clicks "Follow" (the follower)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "follower_id", nullable = false)
    private User follower;

    // The user being followed (the followed)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "followed_id", nullable = false)
    private User followed;

    // Optional: when the follow happened
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    public Follow(User follower, User followed) {
        this.follower = follower;
        this.followed = followed;
    }
}
