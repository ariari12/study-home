package com.project.spring.chatserver.chat.controller;

import com.project.spring.chatserver.chat.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/chat")
public class ChatController {
    private final ChatService chatService;

    @PostMapping("/room/group/create")
    public void createChatRoom(@RequestParam String roomName, Authentication authentication) {
        chatService.creatGroupRoom(roomName, authentication);
    }
}
