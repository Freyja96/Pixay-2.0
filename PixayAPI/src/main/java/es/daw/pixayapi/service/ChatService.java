package es.daw.pixayapi.service;

import io.jsonwebtoken.security.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ChatService {
    @Autowired
    private NotificationService notificationService;


}
