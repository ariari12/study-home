package com.project.spring.chatserver.chat.repository;

import com.project.spring.chatserver.chat.domain.ChatParticipant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatParticipantRepository extends JpaRepository<ChatParticipant, Long> {

}
