package es.daw.pixayapi.service;

import es.daw.pixayapi.entity.*;
import es.daw.pixayapi.repository.CategoryRepository;
import es.daw.pixayapi.repository.ImageRepository;
import es.daw.pixayapi.repository.SavedImageRepository;
import es.daw.pixayapi.repository.SubcategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;

@Service
@RequiredArgsConstructor
public class ImageService {
    private final ImageRepository imageRepository;
    private final CategoryRepository categoryRepository;
    private final SubcategoryRepository subcategoryRepository;
    private final SavedImageRepository savedImageRepository;

    public Slice<Image> getAllImagesPaged(int page, int size){
        Pageable pageable = PageRequest.of(page, size);
        return imageRepository.findAllByOrderByIdDesc(pageable);
    }

    public Slice<Image> getImagesByUser(User user, int page, int size){
        Pageable pageable = PageRequest.of(page, size);
        return imageRepository.findByUser(user, pageable);
    }

    public Slice<Image> getImagesByCategory(String category, Pageable pageable){
        return imageRepository.findByCategory(category, pageable);
    }
    public Image getImageById(Long id) {
        return imageRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Imagen no encontrada con ID: " + id));
    }
    public Image saveImage(MultipartFile file, String title, String category_id, String subcategory_id, User user){
        try {
            Category cat = categoryRepository.findById(Long.parseLong(category_id))
                    .orElseThrow(() -> new RuntimeException("Categoría no encontrada ID: " + category_id));

            Subcategory subcat = null;
            if (subcategory_id != null && !subcategory_id.isEmpty()) {
                subcat = subcategoryRepository.findById(Long.parseLong(subcategory_id)).orElse(null);
            }
            Image image = new Image();
            image.setContent(file.getBytes());
            image.setTitle(title);
            image.setCategory(cat);
            image.setSubcategory(subcat);
            image.setUser(user);

            return imageRepository.save(image);
        } catch (IOException e){
            throw new RuntimeException("Error al leer los bytes de la imagen: ", e);
        }
    }

    public Slice<Image> getSavedImagesByUser(User user, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Slice<SavedImage> savedSlice = savedImageRepository.findByUser(user, pageable);

        return savedSlice.map(SavedImage::getImage);
    }
}