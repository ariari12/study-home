package com.project.spring.chatserver.chat.repository;

import com.project.spring.chatserver.chat.domain.ChatMessage;
import com.project.spring.chatserver.chat.domain.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    List<ChatMessage> findByChatRoomOrderByCreatedTime(ChatRoom chatRoom);
}
