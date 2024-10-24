package com.backend.backenddbp.SocketsConfig.Controller;

import com.backend.backenddbp.Mensaje.Domain.Mensaje;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RealTimeChatController {

    private final SimpMessagingTemplate simpMessagingTemplate;

    @Autowired
    public RealTimeChatController(SimpMessagingTemplate simpMessagingTemplate) {
        this.simpMessagingTemplate = simpMessagingTemplate;
    }

    @MessageMapping("/mensaje")
    @SendTo("/group/public")
    public Mensaje recibirMensaje(@Payload Mensaje mensaje) {
        simpMessagingTemplate.convertAndSend("/group/" + mensaje.getChat().getId().toString(), mensaje);
        return mensaje;
    }

    public void sendMessageToUser(Long userId, Mensaje mensaje) {
        simpMessagingTemplate.convertAndSendToUser(userId.toString(), "/queue/messages", mensaje);
    }
}