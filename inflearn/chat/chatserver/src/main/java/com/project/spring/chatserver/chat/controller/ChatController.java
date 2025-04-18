package com.project.spring.chatserver.chat.controller;

import com.project.spring.chatserver.chat.dto.ChatRoomListResDto;
import com.project.spring.chatserver.chat.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/chat")
public class ChatController {
    private final ChatService chatService;

    @PostMapping("/room/group/create")
    public void createChatRoom(@RequestParam String roomName, Authentication authentication) {
        chatService.creatGroupRoom(roomName, authentication);
    }
    //    그룹 채팅 목록조회
    @GetMapping("/room/group/list")
    public ResponseEntity<?> getGroupChatRooms() {
        List<ChatRoomListResDto> chatRooms = chatService.getGroupChatRooms();
        return new ResponseEntity<>(chatRooms, HttpStatus.OK);
    }

    @PostMapping("/room/group/{roomId}/join")
    public ResponseEntity<?> joinGroupChatRoom(@PathVariable Long roomId, Authentication authentication) {
        chatService.addParticipantToGroup(roomId, authentication);
        return ResponseEntity.ok().build();
    }


}
