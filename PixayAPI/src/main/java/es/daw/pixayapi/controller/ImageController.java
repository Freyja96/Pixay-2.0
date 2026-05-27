package es.daw.pixayapi.controller;

import es.daw.pixayapi.dto.response.ImageResponse;
import es.daw.pixayapi.entity.Image;
import es.daw.pixayapi.entity.ImageReaction;
import es.daw.pixayapi.entity.SavedImage;
import es.daw.pixayapi.entity.User;
import es.daw.pixayapi.repository.ImageReactionRepository;
import es.daw.pixayapi.repository.ImageRepository;
import es.daw.pixayapi.repository.SavedImageRepository;
import es.daw.pixayapi.service.ImageService;
import es.daw.pixayapi.service.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/imagenes")
@RequiredArgsConstructor
public class ImageController {
    private final ImageService imageService;
    private final UserService userService;
    private final ImageRepository imageRepository;
    private final ImageReactionRepository imageReactionRepository;
    private final SavedImageRepository savedImageRepository;
    /**
     * Muestra las imágenes de todos los usuarios.
     *
     * @param page
     * @param size
     * @return
     */
    @GetMapping// <-- página de INICIO
    public ResponseEntity<Slice<ImageResponse>> getAllImages(//<-- para todas las imágenes de todos los usuarios
                                                             @RequestParam(defaultValue = "0") int page,
                                                             @RequestParam(defaultValue = "12") int size
    ) {
        Slice<Image> slice = imageService.getAllImagesPaged(page, size);
        Slice<ImageResponse> responseSlice = slice
                .map(this::convertToResponse);

        return ResponseEntity.ok(responseSlice);
    }

    /**
     * Sube una imagen al servidor.
     *
     * @param content
     * @param title
     * @param category_id
     * @param subcategory_id (opcional)
     * @param userDetails
     * @return
     */
    @PostMapping("/subir-imagen") //<-- se une a continuación de RequestMapping
    //TODO CRIS: cuando Doris tenga el footer bien montado. Poner el PreAuthorize
    //@PreAuthorize("isAuthenticated()")
    public ResponseEntity<ImageResponse> uploadImage(
            @RequestParam("content") MultipartFile content,
            @RequestParam("title") String title,
            @RequestParam("category_id") String category_id,
            @RequestParam(value = "subcategory_id", required = false) String subcategory_id,
            @AuthenticationPrincipal UserDetails userDetails // Obtenemos el usuario del token
    ) {
        if (userDetails == null) {
            System.out.println("ERROR: El usuario llega como NULL. El Token no se ha validado correctamente.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        //pongo traza para ver si el usuario está autenticado
        System.out.println("Usuario que intenta subir: " + userDetails.getUsername());

        if (userDetails == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        User user = userService.findByUsername(userDetails.getUsername());

        Image savedImage = imageService.saveImage(content, title, category_id, subcategory_id, user);

        return ResponseEntity.status(HttpStatus.CREATED).body(convertToResponse(savedImage));
    }

    /**
     * /**
     * Muestra las imágenes del usuario actual.
     *
     * @param user
     * @param page
     * @param size
     * @return
     */
    @GetMapping("/mis-imagenes")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Slice<ImageResponse>> getMyImages(
            @AuthenticationPrincipal User user,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size
    ) { //<-- para las imágenes del usuario actual
        Slice<Image> slice = imageService.getImagesByUser(user, page, size);

        Slice<ImageResponse> responseSlice = slice
                .map(this::convertToResponse);

        return ResponseEntity.ok(responseSlice);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ImageResponse> getImageById(@PathVariable Long id) {
        Image image = imageService.getImageById(id);
        return ResponseEntity.ok(convertToResponse(image));
    }

    /**
     * Muestra las imágenes del usuario por ID
     *
     * @param id
     * @param page
     * @param size
     * @return
     */
    @GetMapping("/usuario/{id}")
    public ResponseEntity<Slice<ImageResponse>> getImagesByUserId(
            @PathVariable Long id,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size
    ) {
        User user = userService.findById(id);

        Slice<Image> slice = imageService.getImagesByUser(user, page, size);
        Slice<ImageResponse> responseSlice = slice.map(this::convertToResponse);

        return ResponseEntity.ok(responseSlice);
    }

    /**
     * Mostrar las imágenes guardadas por el usuario actual.
     *
     * @param userDetails
     * @param page
     * @param size
     * @return
     */
    @GetMapping("/guardadas")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Slice<ImageResponse>> getSavedImages(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size
    ) {
        User user = userService.findByUsername(userDetails.getUsername());
        Slice<Image> slice = imageService.getSavedImagesByUser(user, page, size);
        return ResponseEntity.ok(slice.map(this::convertToResponse));
    }

    /**
     * Imágenes guardadas del usuario por ID.
     *
     * @param id
     * @param page
     * @param size
     * @return
     */
    @GetMapping("/usuario/{id}/guardadas")
    public ResponseEntity<Slice<ImageResponse>> getSavedImagesByUserId(
            @PathVariable Long id,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size
    ) {
        User user = userService.findById(id);
        Slice<Image> slice = imageService.getSavedImagesByUser(user, page, size);
        return ResponseEntity.ok(slice.map(this::convertToResponse));
    }

    @GetMapping("/buscar")
    public ResponseEntity<Slice<ImageResponse>> searchImages(
            @RequestParam(required = false) String query,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Long subcategoryId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());

        String q = (query == null) ? "" : query;
        // Lógica de filtros (Prioridad: Subcategoría > Categoría > Texto)
        if (subcategoryId != null) {
            if (!q.isEmpty())
                return ResponseEntity.ok(imageRepository.findByTitleContainingIgnoreCaseAndSubcategory_Id(q, subcategoryId, pageable).map(this::convertToResponse));
            return ResponseEntity.ok(imageRepository.findBySubcategory_Id(subcategoryId, pageable).map(this::convertToResponse));
        }

        if (categoryId != null) {
            if (!q.isEmpty())
                return ResponseEntity.ok(imageRepository.findByTitleContainingIgnoreCaseAndCategory_Id(q, categoryId, pageable).map(this::convertToResponse));
            return ResponseEntity.ok(imageRepository.findByCategory_Id(categoryId, pageable).map(this::convertToResponse));
        }

        if (!q.isEmpty()) {
            return ResponseEntity.ok(imageRepository.findByTitleContainingIgnoreCase(q, pageable).map(this::convertToResponse));
        }

        return ResponseEntity.ok(imageService.getAllImagesPaged(page, size).map(this::convertToResponse));
    }

    /**
     * Convierte una imagen en una respuesta.
     *
     * @param entity
     * @return
     */
    private ImageResponse convertToResponse(Image entity) {
        return new ImageResponse(
                entity.getId(),
                entity.getTitle(),
                entity.getContent(),
                entity.getUser().getId(),
                entity.getCategory().getName(),
                entity.getSubcategory() != null ? entity.getSubcategory().getName() : "Sin subcategoría" //puede ser nula
        );
    }

    @PostMapping("/{id}/react")
    public ResponseEntity<?> reactToImage(@PathVariable Long id, @RequestBody Map<String, Object> body, @AuthenticationPrincipal UserDetails userDetails) {
        User user = userService.findByUsername(userDetails.getUsername());
        Image image = imageRepository.findById(id).orElseThrow();
        Integer type = Integer.parseInt(body.get("reactionType").toString());

        var existing = imageReactionRepository.findByUserAndImage(user, image);
        String action;

        if (existing.isPresent()) {
            if (existing.get().getReactionType().equals(type)) {
                imageReactionRepository.delete(existing.get());
                action = "removed";
            } else {
                existing.get().setReactionType(type);
                imageReactionRepository.save(existing.get());
                action = "switched";
            }
        } else {
            ImageReaction ir = new ImageReaction();
            ir.setUser(user);
            ir.setImage(image);
            ir.setReactionType(type);
            imageReactionRepository.save(ir);
            action = "added";
        }
        // Devolvemos un mapa con la acción para que el JS sepa qué hacer con los contadores
        return ResponseEntity.ok(Map.of("action", action));
    }
    @GetMapping("/{id}/reactions/count")
    public ResponseEntity<Map<Integer, Long>> getReactionCounts(@PathVariable Long id) {
        List<Object[]> results = imageReactionRepository.countReactionsByImageId(id);
        Map<Integer, Long> counts = new java.util.HashMap<>();
        for (Object[] result : results) {
            counts.put((Integer) result[0], (Long) result[1]);
        }
        return ResponseEntity.ok(counts);
    }

    @PostMapping("/{id}/save")
    @Transactional
    public ResponseEntity<?> toggleSaveImage(@PathVariable Long id, @AuthenticationPrincipal UserDetails userDetails) {
        User user = userService.findByUsername(userDetails.getUsername());
        Image image = imageRepository.findById(id).orElseThrow();

        var savedOpt = savedImageRepository.findByUserAndImage(user, image);
        if (savedOpt.isPresent()) {
            savedImageRepository.delete(savedOpt.get());
            return ResponseEntity.ok(false); // Devuelve false si ya no está guardado
        } else {
            SavedImage si = new SavedImage();
            si.setUser(user);
            si.setImage(image);
            savedImageRepository.save(si);
            return ResponseEntity.ok(true); // Devuelve true si se ha guardado
        }
    }

    // Endpoint para que el detalle sepa si la imagen está guardada al cargar
    @GetMapping("/{id}/status")
    public ResponseEntity<Map<String, Object>> getImageStatus(@PathVariable Long id, @AuthenticationPrincipal UserDetails userDetails) {
        User user = userService.findByUsername(userDetails.getUsername());
        Image image = imageRepository.findById(id).orElseThrow();

        boolean isSaved = savedImageRepository.findByUserAndImage(user, image).isPresent();
        var reaction = imageReactionRepository.findByUserAndImage(user, image);

        return ResponseEntity.ok(Map.of(
                "isSaved", isSaved,
                "reactionType", reaction.isPresent() ? reaction.get().getReactionType() : 0
        ));
    }
}