package es.daw.pixaymvc.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class AppConfig {
    @Value("${api.base-url}") //para leer las propiedades del properties
    private String apiUrl;

    @Value("${api.auth-url}")
    private String authUrl;

    @Bean // son métodos dentro de una clase @Configuration, devuelve un objeto WebClient
    public WebClient webClientAPI(WebClient.Builder builder) {
        return builder
                .baseUrl(apiUrl)
                .codecs(configurer -> configurer
                        .defaultCodecs()
                        .maxInMemorySize(50 * 1024 * 1024)) // 50 MB
                .build();
    }

    @Bean
    public WebClient webClientAuth(WebClient.Builder builder){
        return builder
                .baseUrl(authUrl)
                .build();
    }
}
