package es.daw.pixayapi.entity;

import jakarta.persistence.*;
import lombok.Data;

//id BIGINT AUTO_INCREMENT PRIMARY KEY,
//name VARCHAR(50) NOT NULL UNIQUE,
//category VARCHAR(50) NOT NULL,
@Entity
@Table(name = "subcategories")
@Data
public class Subcategory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 50, nullable = false)
    private String name;

    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;
}