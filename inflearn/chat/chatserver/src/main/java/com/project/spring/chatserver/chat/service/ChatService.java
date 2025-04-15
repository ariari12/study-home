package com.project.spring.chatserver.chat.service;

import com.project.spring.chatserver.chat.domain.ChatMessage;
import com.project.spring.chatserver.chat.domain.ChatParticipant;
import com.project.spring.chatserver.chat.domain.ChatRoom;
import com.project.spring.chatserver.chat.domain.ReadStatus;
import com.project.spring.chatserver.chat.dto.ChatMessageReqDto;
import com.project.spring.chatserver.chat.repository.ChatMessageRepository;
import com.project.spring.chatserver.chat.repository.ChatParticipantRepository;
import com.project.spring.chatserver.chat.repository.ChatRoomRepository;
import com.project.spring.chatserver.chat.repository.ReadStatusRepository;
import com.project.spring.chatserver.common.dto.CustomUser;
import com.project.spring.chatserver.member.domain.Member;
import com.project.spring.chatserver.member.repository.MemberRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class ChatService {
    private final ChatRoomRepository chatRoomRepository;
    private final ChatParticipantRepository chatParticipantRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final ReadStatusRepository readStatusRepository;
    private final MemberRepository memberRepository;

    public void saveMessage(Long roomId, ChatMessageReqDto chatMessageReqDto) {
        // 채팅방 조회
        ChatRoom chatRoom = chatRoomRepository.findById(roomId).orElseThrow(() -> new EntityNotFoundException("채팅방을 찾을 수 없습니다."));
        // 보낸 사람 조회
        Member sender = memberRepository.findByEmail(chatMessageReqDto.getSenderEmail()).orElseThrow(() -> new EntityNotFoundException(" 보낸 사람을 찾을 수 없습니다."));
        // 메시지 저장
        ChatMessage chatMessage = ChatMessage.builder()
                .member(sender)
                .chatRoom(chatRoom)
                .content(chatMessageReqDto.getMessage())
                .build();

        chatMessageRepository.save(chatMessage);


        // 사용자별로 읽음 여부 처리
        chatParticipantRepository.findByChatRoom(chatRoom)
                .forEach(participant -> {
                    ReadStatus readStatus = ReadStatus.builder()
                            .chatRoom(chatRoom)
                            .member(participant.getMember())
                            .chatMessage(chatMessage)
                            .isRead(participant.getMember().equals(sender))
                            .build();
                    readStatusRepository.save(readStatus);
                });


    }

    public void creatGroupRoom(String chatRoomName, Authentication authentication) {
        CustomUser customUser = (CustomUser) authentication.getPrincipal();
        log.info("creatGroupRoom 시작 {}",customUser.getUsername());

        Member member = memberRepository.findByEmail(customUser.getUsername())
                .orElseThrow(() -> new EntityNotFoundException("회원을 찾을 수 없습니다."));

//        채팅방 생성
        ChatRoom chatRoom = ChatRoom.builder()
                .name(chatRoomName)
                .isGroupChat("Y")
                .build();
        chatRoomRepository.save(chatRoom);

//        채팅 참여자로 개설자를 추가
        ChatParticipant participant = ChatParticipant.builder()
                .chatRoom(chatRoom)
                .member(member)
                .build();
        chatParticipantRepository.save(participant);

    }
}
