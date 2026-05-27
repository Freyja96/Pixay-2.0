package es.daw.pixaymvc.controlador;

import es.daw.pixaymvc.dto.response.*;
import es.daw.pixaymvc.service.ImageService;
import jakarta.servlet.http.HttpSession;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Map;

@Controller
public class ImageController {
    private final WebClient webClientAPI;
    private final ImageService imageService;

    public ImageController(WebClient webClientAPI, ImageService imageService) {
        this.webClientAPI = webClientAPI;
        this.imageService = imageService;
    }

    /**
     * Mostrar inicio
     * @param session
     * @param model
     * @return
     */
    @GetMapping("/")
    public String inicio(HttpSession session, Model model) {
        String token = (String) session.getAttribute("token");
        CustomSlice<ImageResponse> slice = imageService.getAllImages(0, 12, token);

        System.out.println("Imágenes recibidas de la API: " + slice.getContent().size());
        if(!slice.getContent().isEmpty()){
            System.out.println("Título de la primera: " + slice.getContent().get(0).title());
        }

        model.addAttribute("imagenes", slice.getContent());
        model.addAttribute("hasNext", slice.isHasNext());

        model.addAttribute("query", "");
        model.addAttribute("selectedCategoryId", null);
        model.addAttribute("selectedSubcategoryId", null);
        model.addAttribute("selectedCategoryName", "Todas las categorías");

        return "pantallas/inicio";
    }
    /**
     * Devuelve datos en formato JSON de las imágenes que se han subido.
     * @param page
     * @param size
     * @param session
     * @return
     */
    @GetMapping("/imagenes")
    @ResponseBody
    public CustomSlice<ImageResponse> getImagenesScroll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size,
            HttpSession session) {

        String token = (String) session.getAttribute("token");
        return imageService.getAllImages(page, size, token);
    }

    /**
     * mostrar el formulario de subida de imagen
     * @param model
     * @return
     */
    @GetMapping("/subir-imagen")
    public String showUploadForm(Model model) {
        cargarSidebar(model);
        return "pantallas/subir-imagen";
    }

    /**
     * Clic en Publicar en el formulario, subir imagen y redirigir a inicio. Si no hay token, no puede subir nada.
     * @param content
     * @param title
     * @param category_id
     * @param subcategory_id
     * @param redirectAttributes
     * @param session
     * @return
     */
    @PostMapping("/subir-imagen")
    public String handleFileUpload(@RequestParam("content") MultipartFile content,
                                   @RequestParam("title") String title,
                                   @RequestParam("category_id") String category_id,
                                   @RequestParam(value = "subcategory_id", required = false) String subcategory_id,
                                   RedirectAttributes redirectAttributes,
                                   HttpSession session
                                   //Model model
    ) {
        String token = (String) session.getAttribute("token");
        if (token == null) return "redirect:/login";

        MultipartBodyBuilder builder = new MultipartBodyBuilder();
        builder.part("content", content.getResource());
        builder.part("title", title);
        builder.part("category_id", category_id);
        if (subcategory_id != null) builder.part("subcategory_id", subcategory_id);

        try {
            webClientAPI.post()
                    .uri("imagenes/subir-imagen")
                    .header("Authorization", "Bearer " + token)
                    .body(BodyInserters.fromMultipartData(builder.build()))
                    .retrieve()
                    .bodyToMono(ImageResponse.class)
                    .block();
            redirectAttributes.addFlashAttribute("message", "¡Imagen subida con éxito!");
            return "redirect:/";
        } catch (Exception e) {
            System.out.println("Error subiendo: " + e.getMessage());
            redirectAttributes.addFlashAttribute("error", "Error al conectar con la API: " + e.getMessage());
            return "redirect:/subir-imagen";
        }
    }

    /**
     * Muestra las imágenes del perfil del usuario actual. Si no hay token, redirige a login.
     * @param session
     * @param model
     * @return
     */
    @GetMapping("/mi-perfil/mis-imagenes")
    public String misImagenes(HttpSession session, Model model) {
        String token = (String) session.getAttribute("token");
        if (token == null) return "redirect:/login";

        UserProfileResponse profile = webClientAPI.get()
                .uri("usuarios/me")
                .headers(h -> h.setBearerAuth(token))
                .retrieve()
                .bodyToMono(UserProfileResponse.class)
                .block();

        System.out.println("DEBUB mis-imagenes: username= " + profile.username());
        System.out.println("DEBUB mis-imagenes: desciption=" + profile.description());
        System.out.println("DEBUB mis-imagenes: perfilPublico=" + profile.publicProfile());


        CustomSlice<ImageResponse> slice = webClientAPI.get()
                .uri(uriBuilder -> uriBuilder
                        .path("imagenes/usuario/" + profile.id())
                        .queryParam("page", 0)
                        .queryParam("size", 12)
                        .build())
                .headers(h -> h.setBearerAuth(token))
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<CustomSlice<ImageResponse>>() {})
                .block();

        model.addAttribute("usuario", profile);
        model.addAttribute("imagenes", slice.getContent());
        model.addAttribute("hasNext", slice.isHasNext());

        model.addAttribute("query", "");
        model.addAttribute("selectedCategoryId", null);
        model.addAttribute("selectedSubcategoryId", null);
        model.addAttribute("selectedCategoryName", "Todas las categorías");

        return "pantallas/mi-perfil/mis-imagenes";
    }

    /**
     * Scroll de las imágenes del perfil del usuario actual. Es el endpoint que usará el FETCH del JavaScript
     * @param userId
     * @param page
     * @param size
     * @param session
     * @return
     */
    @GetMapping("/mi-perfil/mis-imagenes/scroll")
    @ResponseBody
    public CustomSlice<ImageResponse> getMisImagenesScroll(
            @RequestParam Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size,
            HttpSession session) {

        String token = (String) session.getAttribute("token");

        return webClientAPI.get()
                .uri(uriBuilder -> uriBuilder
                        .path("imagenes/usuario/" + userId)
                        .queryParam("page", page)
                        .queryParam("size", size)
                        .build())
                .headers(h -> h.setBearerAuth(token))
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<CustomSlice<ImageResponse>>() {})
                .block();
    }

    /**
     * Para mostrar las imágenes guardadas del usuario actual. Si no hay token, redirige a login.
     * @param session
     * @param model
     * @return
     */
    @GetMapping("/mi-perfil/guardadas")
    public String misImagenesGuardadas(HttpSession session, Model model) {
        String token = (String) session.getAttribute("token");
        if (token == null) return "redirect:/login";

        UserProfileResponse profile = webClientAPI.get()
                .uri("usuarios/me").headers(h -> h.setBearerAuth(token))
                .retrieve().bodyToMono(UserProfileResponse.class).block();

        CustomSlice<ImageResponse> slice = webClientAPI.get()
                .uri(uriBuilder -> uriBuilder.path("imagenes/guardadas")
                        .queryParam("page", 0).queryParam("size", 12).build())
                .headers(h -> h.setBearerAuth(token))
                .retrieve().bodyToMono(new ParameterizedTypeReference<CustomSlice<ImageResponse>>() {})
                .block();

        model.addAttribute("usuario", profile);
        model.addAttribute("imagenes", slice.getContent());
        model.addAttribute("hasNext", slice.isHasNext());
        model.addAttribute("seccion", "guardadas");

        model.addAttribute("query", "");
        model.addAttribute("selectedCategoryId", null);
        model.addAttribute("selectedSubcategoryId", null);
        model.addAttribute("selectedCategoryName", "Todas las categorías");

        return "pantallas/mi-perfil/mis-imagenes";
    }

    /**
     * Endpoint para el scroll de guardadas
     * @param page
     * @param session
     * @return
     */
    @GetMapping("/mi-perfil/guardadas/scroll")
    @ResponseBody
    public CustomSlice<ImageResponse> getSavedScroll(
            @RequestParam int page, HttpSession session) {
        String token = (String) session.getAttribute("token");
        return webClientAPI.get()
                .uri(uriBuilder -> uriBuilder.path("imagenes/guardadas")
                        .queryParam("page", page).queryParam("size", 12).build())
                .headers(h -> h.setBearerAuth(token))
                .retrieve().bodyToMono(new ParameterizedTypeReference<CustomSlice<ImageResponse>>() {})
                .block();
    }

    /**
     * Mostrar el perfil de otro usuario por ID, con sus imágenes.
     * Si el perfil es público, se muestra aunque no haya token.
     * Si no es público, se muestra solo si hay token.
     * @param id
     * @param session
     * @param model
     * @return
     */
    @GetMapping("/usuario/{id}")
    public String verPerfilAjeno(@RequestParam(required = false) String query,
                                 @RequestParam(required = false) Long categoryId,
                                 @RequestParam(required = false) Long subcategoryId,
                                 @PathVariable Long id,
                                 HttpSession session,
                                 Model model) {
        String token = (String) session.getAttribute("token");
        // token opcional, por si queremos que los perfiles sean públicos

        UserProfileResponse profile = webClientAPI.get()
                .uri("usuarios/" + id)
                .headers(h -> { if(token != null) h.setBearerAuth(token); })
                .retrieve()
                .bodyToMono(UserProfileResponse.class)
                .block();

        CustomSlice<ImageResponse> slice = webClientAPI.get()
                .uri(uriBuilder -> uriBuilder
                        .path("imagenes/usuario/" + id)
                        .queryParam("page", 0)
                        .queryParam("size", 12)
                        .build())
                .headers(h -> { if(token != null) h.setBearerAuth(token); })
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<CustomSlice<ImageResponse>>() {})
                .block();

        model.addAttribute("query", query != null ? query : "");
        model.addAttribute("selectedCategoryId", categoryId);
        model.addAttribute("selectedSubcategoryId", subcategoryId);
        model.addAttribute("selectedCategoryName", "Todas las categorías");

        model.addAttribute("usuario", profile);
        model.addAttribute("imagenes", slice.getContent());
        model.addAttribute("hasNext", slice.isHasNext());

        return "pantallas/mi-perfil/mis-imagenes";
    }

    /**
     * Mostrar las imágenes guardadas de otro usuario por ID.
     * @param id
     * @param session
     * @param model
     * @return
     */
    @GetMapping("/usuario/{id}/guardadas")
    public String verGuardadasAjeno(@PathVariable Long id, HttpSession session, Model model) {
        String token = (String) session.getAttribute("token");

        UserProfileResponse profile = webClientAPI.get()
                .uri("usuarios/" + id).headers(h -> { if(token!=null) h.setBearerAuth(token); })
                .retrieve().bodyToMono(UserProfileResponse.class).block();

        CustomSlice<ImageResponse> slice = webClientAPI.get()
                .uri(uriBuilder -> uriBuilder.path("imagenes/usuario/" + id + "/guardadas").build())
                .headers(h -> { if(token!=null) h.setBearerAuth(token); })
                .retrieve().bodyToMono(new ParameterizedTypeReference<CustomSlice<ImageResponse>>() {})
                .block();

        model.addAttribute("usuario", profile);
        model.addAttribute("imagenes", slice.getContent());
        model.addAttribute("hasNext", slice.isHasNext());
        model.addAttribute("seccion", "guardadas"); // Para que el botón se vea azul

        model.addAttribute("query", "");
        model.addAttribute("selectedCategoryId", null);
        model.addAttribute("selectedSubcategoryId", null);
        model.addAttribute("selectedCategoryName", "Todas las categorías");

        return "pantallas/mi-perfil/mis-imagenes";
    }

    /**
     * Mostrar el detalle de una imagen por ID. Incluye chat.
     * @param id
     * @param session
     * @param model
     * @return
     */
    @GetMapping("/imagen/{id}")
    public String verDetalleImagen(@PathVariable Long id, HttpSession session, Model model) {
        String token = (String) session.getAttribute("token");

        ImageResponse imagen = imageService.getImageById(id, token);
        model.addAttribute("imagen", imagen);

        List<CommentResponse> comentarios = webClientAPI.get()
                .uri("imagenes/" + id + "/comentarios")
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<CommentResponse>>() {})
                .block();


        model.addAttribute("comentarios", comentarios);

        UserProfileResponse autor = webClientAPI.get()
                .uri("usuarios/" + imagen.userId())
                .retrieve()
                .bodyToMono(UserProfileResponse.class)
                .block();
        model.addAttribute("autor", autor);


        try {
            Map<String, Integer> counts = webClientAPI.get()
                    .uri("imagenes/" + id + "/reactions/count")
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<Map<String, Integer>>() {})
                    .block();
            model.addAttribute("counts", counts);
        } catch (Exception e) {
            // Si falla el endpoint, envia un mapa vacío
            model.addAttribute("counts", Map.of());
        }

        // Configuración de búsqueda/sidebar
        model.addAttribute("query", "");
        model.addAttribute("selectedCategoryId", null);
        model.addAttribute("selectedSubcategoryId", null);
        model.addAttribute("selectedCategoryName", "Todas las categorías");

        if (token != null) {
            try {
                Map<String, Object> status = webClientAPI.get()
                        .uri("imagenes/" + id + "/status")
                        .header("Authorization", "Bearer " + token)
                        .retrieve()
                        .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                        .block();

                model.addAttribute("isSaved", status.get("isSaved"));
                model.addAttribute("currentReaction", status.get("reactionType"));
            } catch (Exception e) {
                model.addAttribute("isSaved", false);
                model.addAttribute("currentReaction", 0);
            }
        } else {
            model.addAttribute("isSaved", false);
            model.addAttribute("currentReaction", 0);
        }

        return "pantallas/detalle";
    }

    /**
     * Mostrar los resultados de la búsqueda por título. Si no hay query, muestra todas las imágenes.
     * @param query
     * @param model
     * @param session
     * @return
     */
    @GetMapping("/busqueda")
    public String mostrarBusqueda(@RequestParam(required = false) String query,
                                  @RequestParam(required = false) Long categoryId,
                                  @RequestParam(required = false) Long subcategoryId,
                                  Model model,
                                  HttpSession session
    ) {
        String token = (String) session.getAttribute("token");

        CustomSlice<ImageResponse> slice = webClientAPI.get()
                .uri(uriBuilder -> {
                    uriBuilder.path("imagenes/buscar")
                            .queryParam("page", 0)
                            .queryParam("size", 12);
                    if (query != null && !query.isEmpty()) uriBuilder.queryParam("query", query);
                    if (categoryId != null) uriBuilder.queryParam("categoryId", categoryId);
                    if (subcategoryId != null) uriBuilder.queryParam("subcategoryId", subcategoryId);
                    return uriBuilder.build();
                })
                .headers(h -> { if (token != null) h.setBearerAuth(token); })
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<CustomSlice<ImageResponse>>() {})
                .block();

        model.addAttribute("query", query != null ? query : "");
        model.addAttribute("selectedCategoryId", categoryId);
        model.addAttribute("selectedSubcategoryId", subcategoryId);

        model.addAttribute("imagenes", slice.getContent());
        model.addAttribute("hasNext", slice.isHasNext());

        String catName = "Todas las categorías";
        if (categoryId != null) {
            // Obtenemos las categorías de nuevo o las filtramos de la lista que ya pides en cargarSidebar
            // Para simplificar, si el ID es 1 es Fotografía, si es 2 Ilustración (según tu data.sql)
            catName = (categoryId == 1) ? "Fotografía" : (categoryId == 2 ? "Ilustración" : "Todas las categorías");
        }
        model.addAttribute("selectedCategoryName", catName);

        //Categorías y subcategorías en el sidebar:
        cargarSidebar(model);
        return "pantallas/busqueda";
    }

    /**
     * Scroll de la pantalla de búsqueda. Es el endpoint que usará el FETCH del JavaScript
     * @param query
     * @param page
     * @param session
     * @return
     */
    @GetMapping("/busqueda/scroll")
    @ResponseBody
    public CustomSlice<ImageResponse> getBusquedaScroll(
            @RequestParam(required = false) String query,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Long subcategoryId,
            @RequestParam int page, HttpSession session) {
        String token = (String) session.getAttribute("token");

        String path = (query != null || categoryId != null || subcategoryId != null)
                ? "imagenes/buscar"
                : "imagenes";

        return webClientAPI.get()
                .uri(uriBuilder -> {
                    uriBuilder.path(path).queryParam("page", page).queryParam("size", 12);
                    if(query != null) uriBuilder.queryParam("query", query);
                    if(categoryId != null) uriBuilder.queryParam("categoryId", categoryId);
                    if(subcategoryId != null) uriBuilder.queryParam("subcategoryId", subcategoryId);
                    return uriBuilder.build();
                })
                .headers(h -> { if(token!=null) h.setBearerAuth(token); })
                .retrieve().bodyToMono(new ParameterizedTypeReference<CustomSlice<ImageResponse>>() {}).block();
    }
    private void cargarSidebar(Model model) {
        List<CategoryResponse> categories = webClientAPI.get()
                .uri("categories")
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<CategoryResponse>>() {})
                .block();

        List<SubcategoryResponse> subcategories = webClientAPI.get()
                .uri("subcategories")
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<SubcategoryResponse>>() {})
                .block();

        model.addAttribute("categoriesObjects", categories);
        model.addAttribute("subcategoriesObjects", subcategories);
    }

    @GetMapping("/imagen/{id}/descargar")
    public ResponseEntity<byte[]> descargarImagen(@PathVariable Long id, HttpSession session) {
        String token = (String) session.getAttribute("token");
        if (token == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        try {
            ResponseEntity<byte[]> apiResponse = webClientAPI.get()
                    .uri("imagenes/" + id + "/download")
                    .headers(h -> h.setBearerAuth(token))
                    .retrieve()
                    .toEntity(byte[].class)
                    .block();

            if (apiResponse == null || apiResponse.getBody() == null) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }

            String disposition = apiResponse.getHeaders().getFirst(HttpHeaders.CONTENT_DISPOSITION);
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            disposition != null ? disposition : "attachment; filename=\"imagen-" + id + ".jpg\"")
                    .body(apiResponse.getBody());
        } catch (WebClientResponseException.Forbidden e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }
    @PostMapping("/imagen/{id}/comentario")
    @ResponseBody
    public ResponseEntity<?> guardarComentarioProxy(@PathVariable Long id,
                                                    @RequestBody Map<String, String> body,
                                                    HttpSession session) {
        String token = (String) session.getAttribute("token");
        if (token == null) return ResponseEntity.status(401).build();

        // El MVC le envía el mensaje a la API (8081)
        return webClientAPI.post()
                .uri("imagenes/" + id + "/comentarios")
                .header("Authorization", "Bearer " + token)
                .bodyValue(body)
                .retrieve()
                .toBodilessEntity()
                .block();
    }
    @PostMapping("/imagen/{id}/save")
    @ResponseBody
    public ResponseEntity<?> proxySave(@PathVariable Long id, HttpSession session) {
        String token = (String) session.getAttribute("token");
        if (token == null) return ResponseEntity.status(401).build();
        return webClientAPI.post().uri("imagenes/" + id + "/save").header("Authorization", "Bearer " + token).retrieve().toBodilessEntity().block();
    }

    @PostMapping("/imagen/{id}/react")
    @ResponseBody
    public ResponseEntity<?> proxyReact(@PathVariable Long id, @RequestBody Map<String, String> body, HttpSession session) {
        String token = (String) session.getAttribute("token");
        if (token == null) return ResponseEntity.status(401).build();
        return webClientAPI.post().uri("imagenes/" + id + "/react").header("Authorization", "Bearer " + token).bodyValue(body).retrieve().toBodilessEntity().block();
    }
}