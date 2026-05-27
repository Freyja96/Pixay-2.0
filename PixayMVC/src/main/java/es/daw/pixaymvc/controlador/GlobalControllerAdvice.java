package es.daw.pixaymvc.controlador;

import es.daw.pixaymvc.dto.response.CategoryResponse;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

/**
 * Para inyectar la lista de categorías en el modelo de todas las páginas automáticamente
 */
@ControllerAdvice
public class GlobalControllerAdvice {

    private final WebClient webClientAPI;

    public GlobalControllerAdvice(WebClient webClientAPI) {
        this.webClientAPI = webClientAPI;
    }

    // hace que "headerCategories" esté disponible en TODAS las plantillas
    @ModelAttribute("headerCategories")
    public List<CategoryResponse> getHeaderCategories() {
        try {
            return webClientAPI.get()
                    .uri("categories")
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<List<CategoryResponse>>() {})
                    .block();
        } catch (Exception e) {
            System.err.println("Error cargando categorías para el header: " + e.getMessage());
            return List.of(); // Devuelve lista vacía si falla la API
        }
    }
}