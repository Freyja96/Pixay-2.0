package es.daw.pixayapi.entity;

import jakarta.persistence.*;
import lombok.Data;

//id BIGINT AUTO_INCREMENT PRIMARY KEY,
//name VARCHAR(50) NOT NULL UNIQUE
@Entity
@Table(name = "categories")
@Data
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 50, unique = true, nullable = false)
    private String name;
}