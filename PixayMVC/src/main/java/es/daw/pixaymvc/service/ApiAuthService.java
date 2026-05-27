package es.daw.pixaymvc.service;

import es.daw.pixaymvc.dto.ApiLoginRequest;
import es.daw.pixaymvc.dto.ApiLoginResponse;
import es.daw.pixaymvc.dto.RegisterRequest;
import es.daw.pixaymvc.exception.ConnectApiRestException;
import es.daw.pixaymvc.session.ApiSessionToken;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class ApiAuthService {
    private final WebClient webClientAuth;
    private final ApiSessionToken apiSessionToken;

    public String login(String username, String password) {
        ApiLoginRequest request = new ApiLoginRequest();
        request.setUsername(username);
        request.setPassword(password);

        try {
            ApiLoginResponse response = webClientAuth
                    .post()
                    .uri("login")
                    .bodyValue(request)
                    .retrieve()
                    .onStatus(HttpStatusCode::isError, errorResponse -> {
                        System.out.println("Error de la API en Login: " + errorResponse.statusCode());
                        return errorResponse.bodyToMono(String.class).map(Exception::new);
                    })
                    .bodyToMono(ApiLoginResponse.class)
                    .block();

            if (response != null && response.getToken() != null) {
                apiSessionToken.setApiToken(response.getToken());
                return response.getToken();
            }
            return null;
        } catch (Exception ex) {
            throw new ConnectApiRestException("Error en el login: " + ex.getMessage());
        }
    }
    public void registro(String username, String email, String password) throws Exception {
        webClientAuth.post()
                .uri("registro")
                .bodyValue(new RegisterRequest(username, email, password))
                .retrieve()
                .onStatus(HttpStatusCode::isError, response ->
                        response.bodyToMono(String.class).flatMap(error -> Mono.error(new Exception(error)))
                )
                .bodyToMono(Void.class)
                .block();
    }
}
