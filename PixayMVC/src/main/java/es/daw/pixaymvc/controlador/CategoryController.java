package es.daw.pixaymvc.controlador;

import es.daw.pixaymvc.entity.Category;
import es.daw.pixaymvc.entity.Subcategory;
import es.daw.pixaymvc.repository.CategoryRepository;
import es.daw.pixaymvc.repository.SubcategoryRepository;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class CategoryController {
    private final CategoryRepository categoryRepository;
    private final SubcategoryRepository subcategoryRepository;

    public CategoryController(CategoryRepository categoryRepository, SubcategoryRepository subcategoryRepository) {
        this.categoryRepository = categoryRepository;
        this.subcategoryRepository = subcategoryRepository;
    }

    @GetMapping("/categories")
    public List<String> getCategories() {
        return categoryRepository.findAll().stream().map(Category::getName).toList();
    }

    @GetMapping("/subcategories")
    public List<String> getSubcategories() {
        return subcategoryRepository.findAll().stream().map(Subcategory::getName).toList();
    }
}
