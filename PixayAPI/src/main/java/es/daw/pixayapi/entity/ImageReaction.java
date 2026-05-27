package es.daw.pixayapi.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "image_reactions")
@Data
public class ImageReaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "image_id", nullable = false)
    private Image image;

    private Integer reactionType;
}