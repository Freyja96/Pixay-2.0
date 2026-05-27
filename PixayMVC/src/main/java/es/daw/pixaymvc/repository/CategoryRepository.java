package es.daw.pixaymvc.repository;

import es.daw.pixaymvc.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    List<Category> findAll();
}
