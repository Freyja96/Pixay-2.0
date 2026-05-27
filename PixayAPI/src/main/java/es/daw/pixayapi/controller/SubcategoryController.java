package es.daw.pixayapi.controller;

import es.daw.pixayapi.dto.response.SubcategoryResponse;
import es.daw.pixayapi.repository.SubcategoryRepository;
import es.daw.pixayapi.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/subcategories")
@RequiredArgsConstructor
public class SubcategoryController {
    private final CategoryService categoryService;
    @GetMapping
    public ResponseEntity<List<SubcategoryResponse>> getSubcategorias() {
        List<SubcategoryResponse> response = categoryService.findAllSubcategories().stream()
                .map(sub -> new SubcategoryResponse(
                        sub.getId(),
                        sub.getName(),
                        sub.getCategory().getId()
                ))
                .toList();
        return ResponseEntity.ok(response);
    }
}
