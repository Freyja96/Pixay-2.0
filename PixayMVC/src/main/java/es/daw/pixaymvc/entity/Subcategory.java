package es.daw.pixaymvc.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Subcategory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true,
            nullable = false)
    private String name;

    @Column(nullable = false)
    private String category;
}
