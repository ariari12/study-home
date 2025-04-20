package com.project.spring.chatserver.chat.controller;

import com.project.spring.chatserver.chat.dto.ChatMessageDto;
import com.project.spring.chatserver.chat.dto.ChatRoomListResDto;
import com.project.spring.chatserver.chat.dto.MyChatListResDto;
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

//    이전 메세지 조회
    @GetMapping("/history/{roomId}")
    public ResponseEntity<?> getChatHistory(@PathVariable Long roomId, Authentication auth) {
        List<ChatMessageDto> chatMessageDtos = chatService. getChatHistory(roomId, auth);
        return new ResponseEntity<>(chatMessageDtos, HttpStatus.OK);
    }
//    내 채팅방 목록 조회 : roomId, roomName, 그룹채팅 여부, 메시지 읽음 개수
    @GetMapping("/my/rooms")
    public ResponseEntity<?> getMyChatRooms(Authentication auth) {
        List<MyChatListResDto> myChatListResDtos = chatService.getMyChatRooms(auth);
        return new ResponseEntity<>(myChatListResDtos, HttpStatus.OK);
    }
//    채팅메세지 읽음 처리
    @PostMapping("/room/{roomId}/read")
    public ResponseEntity<?> messageRead(@PathVariable Long roomId, Authentication auth) {
        chatService.messageRead(roomId, auth);
        return ResponseEntity.ok().build();
    }
//    채팅방 나가기
    @DeleteMapping("/room/group/{roomId}/leave")
    public ResponseEntity<?> leaveGroupChatRoom(Authentication auth, @PathVariable Long roomId) {
        chatService.leaveGroupChatRoom(roomId, auth);
        return ResponseEntity.ok().build();
    }


}
