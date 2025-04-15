package com.project.spring.chatserver.chat.repository;

import com.project.spring.chatserver.chat.domain.ChatParticipant;
import com.project.spring.chatserver.chat.domain.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatParticipantRepository extends JpaRepository<ChatParticipant, Long> {

    List<ChatParticipant> findByChatRoom(ChatRoom chatRoom);
}
