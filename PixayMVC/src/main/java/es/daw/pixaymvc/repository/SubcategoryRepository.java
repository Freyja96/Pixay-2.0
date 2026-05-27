package es.daw.pixaymvc.repository;

import es.daw.pixaymvc.entity.Subcategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SubcategoryRepository extends JpaRepository<Subcategory, Long> {
    List<Subcategory> findAll();
}
