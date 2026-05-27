package es.daw.pixayapi.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // "/topic" = where the server sends messages to clients
        config.enableSimpleBroker("/topic");
        // "/app" = where clients send messages to the server (not needed for simple notifications)
        config.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // Browser connects here: ws://localhost:8081/ws
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("http://localhost:8080", "http://127.0.0.1:8080")
                .withSockJS(); // fallback for old browsers
    }

}
