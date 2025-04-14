package com.project.spring.chatserver.chat.service;

import com.project.spring.chatserver.chat.repository.ChatMessageRepository;
import com.project.spring.chatserver.chat.repository.ChatParticipantRepository;
import com.project.spring.chatserver.chat.repository.ChatRoomRepository;
import com.project.spring.chatserver.chat.repository.ReadStatusRepository;
import com.project.spring.chatserver.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class ChatService {
    private final ChatRoomRepository chatRoomRepository;
    private final ChatParticipantRepository chatParticipantRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final ReadStatusRepository readStatusRepository;
    private final MemberRepository memberRepository;
}
