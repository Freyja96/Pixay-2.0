package es.daw.pixaymvc.service;

import es.daw.pixaymvc.dto.response.CustomSlice;
import es.daw.pixaymvc.dto.response.ImageResponse;
import es.daw.pixaymvc.session.ApiSessionToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class ImageService {
    @Autowired
    private WebClient webClientAPI;

    public CustomSlice<ImageResponse> getAllImages(int page, int size, String token) {
        System.out.println("Solicitando a la API: Página " + page + " con tamaño " + size);
        return webClientAPI.get()
                .uri(uriBuilder -> uriBuilder
                        .path("imagenes")
                        .queryParam("page", page)
                        .queryParam("size", size)
                        .build())
                .headers(headers -> {
                    if (token != null && !token.isEmpty()) {
                        headers.setBearerAuth(token);
                    }
                })
                .retrieve()
                .onStatus(HttpStatusCode::isError, response -> {
                    // ESTO TE DIRÁ EL ERROR REAL EN LA CONSOLA
                    System.out.println("Error de la API: " + response.statusCode());
                    return response.bodyToMono(String.class).map(Exception::new);
                })
                .bodyToMono(new ParameterizedTypeReference<CustomSlice<ImageResponse>>() {})
                .block();
    }

    public ImageResponse getImageById(Long id, String token) {
        return webClientAPI.get()
                .uri("imagenes/" + id)
                .headers(headers -> {
                    if (token != null) headers.setBearerAuth(token);
                })
                .retrieve()
                .bodyToMono(ImageResponse.class)
                .block();
    }
    //getAllImagesFromUser
}