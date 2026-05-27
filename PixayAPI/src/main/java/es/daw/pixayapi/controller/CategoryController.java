package es.daw.pixayapi.controller;

import es.daw.pixayapi.dto.response.CategoryResponse;
import es.daw.pixayapi.entity.Category;
import es.daw.pixayapi.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryService categoryService;

    @GetMapping("/categories")
    public ResponseEntity<List<CategoryResponse>> getCategorias() {
        List<CategoryResponse> response = categoryService.findAll().stream()
                .map(cat -> new CategoryResponse(cat.getId(), cat.getName()))
                .toList();
        return ResponseEntity.ok(response);
    }
}