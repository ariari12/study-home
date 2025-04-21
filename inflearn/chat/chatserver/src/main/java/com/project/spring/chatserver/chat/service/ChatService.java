package com.project.spring.chatserver.chat.service;

import com.project.spring.chatserver.chat.domain.ChatMessage;
import com.project.spring.chatserver.chat.domain.ChatParticipant;
import com.project.spring.chatserver.chat.domain.ChatRoom;
import com.project.spring.chatserver.chat.domain.ReadStatus;
import com.project.spring.chatserver.chat.dto.ChatMessageDto;
import com.project.spring.chatserver.chat.dto.ChatRoomListResDto;
import com.project.spring.chatserver.chat.dto.MyChatListResDto;
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

import java.util.List;
import java.util.Optional;

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

    public void saveMessage(Long roomId, ChatMessageDto chatMessageDto) {
        // 채팅방 조회
        ChatRoom chatRoom = chatRoomRepository.findById(roomId).orElseThrow(() -> new EntityNotFoundException("채팅방을 찾을 수 없습니다."));
        // 보낸 사람 조회
        Member sender = memberRepository.findByEmail(chatMessageDto.getSenderEmail()).orElseThrow(() -> new EntityNotFoundException(" 보낸 사람을 찾을 수 없습니다."));
        // 메시지 저장
        ChatMessage chatMessage = ChatMessage.builder()
                .member(sender)
                .chatRoom(chatRoom)
                .content(chatMessageDto.getMessage())
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

    public List<ChatRoomListResDto> getGroupChatRooms() {
        List<ChatRoom> chatRooms = chatRoomRepository.findByIsGroupChat("Y");
        List<ChatRoomListResDto> dtos = chatRooms.stream().map(
                chatRoom -> ChatRoomListResDto.builder()
                        .roomId(chatRoom.getId())
                        .roomName(chatRoom.getName())
                        .build()
        ).toList();
        return dtos;
    }

    public void addParticipantToGroup(Long roomId, Authentication authentication) {
        // 회원 조회
        CustomUser customUser = (CustomUser) authentication.getPrincipal();
        Member member = memberRepository.findByEmail(customUser.getUsername()).orElseThrow(() -> new EntityNotFoundException("회원을 찾을 수 없습니다."));
        // 채팅방 조회
        ChatRoom chatRoom = chatRoomRepository.findById(roomId).orElseThrow(() -> new EntityNotFoundException("방을 찾을 수 없습니다."));

//        그룹채팅인지 검증
        if(!chatRoom.getIsGroupChat().equals("Y")) {
            throw new IllegalArgumentException("그룹 채팅이 아닙니다.");
        }

        // 참여자 검증 후 저장
        Optional<ChatParticipant> participant = chatParticipantRepository.findByChatRoomAndMember(chatRoom, member);
        if (!participant.isPresent()) {
            addParticipantToRoom(chatRoom, member);
        }
    }

//    chatParticipant 객체 생성 후 저장
    public void addParticipantToRoom(ChatRoom chatRoom, Member member) {
        ChatParticipant participant = ChatParticipant.builder()
                .chatRoom(chatRoom)
                .member(member)
                .build();
        chatParticipantRepository.save(participant);;
    }

    public List<ChatMessageDto> getChatHistory(Long roomId, Authentication auth) {
        CustomUser customUser = (CustomUser) auth.getPrincipal();
        Member member = memberRepository.findByEmail(customUser.getUsername()).orElseThrow(() -> new EntityNotFoundException("회원을 찾을 수 없습니다."));
        ChatRoom chatRoom = chatRoomRepository.findById(roomId).orElseThrow(() -> new EntityNotFoundException("방을 찾을 수 없습니다."));

        chatParticipantRepository.findByChatRoomAndMember(chatRoom, member).orElseThrow(
                () -> new EntityNotFoundException("채팅방 참여자가 아닙니다.")
        );

        List<ChatMessage> chatMessages = chatMessageRepository.findByChatRoomOrderByCreatedTime(chatRoom);

        return chatMessages.stream().map(
                chatMessage -> ChatMessageDto.builder()
                        .message(chatMessage.getContent())
                        .senderEmail(chatMessage.getMember().getEmail())
                        .build()
        ).toList();
    }

    public boolean isRoomParticipant(String email, String roomId) {
        Member member = memberRepository.findByEmail(email).orElseThrow(() -> new EntityNotFoundException("회원을 찾을 수 없습니다."));
        ChatRoom chatRoom = chatRoomRepository.findById(Long.parseLong(roomId)).orElseThrow(() -> new EntityNotFoundException("방을 찾을 수 없습니다."));

        return chatParticipantRepository.existsChatParticipantByChatRoomAndMember(chatRoom, member);
    }

//    채팅 메세지 읽음 처리
    public void messageRead(Long roomId, Authentication auth) {
        CustomUser customUser = (CustomUser) auth.getPrincipal();
        Member member = memberRepository.findByEmail(customUser.getUsername()).orElseThrow(() -> new EntityNotFoundException("회원을 찾을 수 없습니다."));
        ChatRoom chatRoom = chatRoomRepository.findById(roomId).orElseThrow(() -> new EntityNotFoundException("방을 찾을 수 없습니다."));

        List<ReadStatus> readStatuses = readStatusRepository.findByChatRoomAndMember(chatRoom, member);
        readStatuses.forEach(readStatus -> {
            readStatus.updateIsRead(true);
        });
    }

    public List<MyChatListResDto> getMyChatRooms(Authentication auth) {
        CustomUser customUser = (CustomUser) auth.getPrincipal();
        Member member = memberRepository.findByEmail(customUser.getUsername()).orElseThrow(() -> new EntityNotFoundException("회원을 찾을 수 없습니다."));

        List<ChatParticipant> chatParticipants = chatParticipantRepository.findAllByMember(member);

        return chatParticipants.stream()
                .map(c -> {
                    Long count = readStatusRepository.countByChatRoomAndMemberAndIsReadFalse(c.getChatRoom(), c.getMember());
                    return MyChatListResDto.builder()
                            .roomId(c.getChatRoom().getId())
                            .roomName(c.getChatRoom().getName())
                            .isGroupChat(c.getChatRoom().getIsGroupChat())
                            .unreadCount(count)
                            .build();
                }).toList();
    }

    public void leaveGroupChatRoom(Long roomId, Authentication auth) {
        CustomUser customUser = (CustomUser) auth.getPrincipal();
        Member member = memberRepository.findByEmail(customUser.getUsername()).orElseThrow(() -> new EntityNotFoundException("회원을 찾을 수 없습니다."));
        ChatRoom chatRoom = chatRoomRepository.findById(roomId).orElseThrow(() -> new EntityNotFoundException("방을 찾을 수 없습니다."));
        if(!chatRoom.getIsGroupChat().equals("Y")){
            throw new IllegalArgumentException("그룹 채팅방이 아닙니다.");
        }
        ChatParticipant chatParticipant = chatParticipantRepository.findByChatRoomAndMember(chatRoom, member).orElseThrow(() -> new EntityNotFoundException("채팅방 참여자가 아닙니다."));
        chatParticipantRepository.delete(chatParticipant);
//        참여자가 아무도 없다면
        long countByChatRoom = chatParticipantRepository.countByChatRoom(chatRoom);
        if(countByChatRoom == 0){
            chatRoomRepository.delete(chatRoom);
        }


    }

    public Long getOrCreatePrivateRoom(Long otherMemberId, Authentication auth) {
        CustomUser customUser = (CustomUser) auth.getPrincipal();
        Member member = memberRepository.findByEmail(customUser.getUsername()).orElseThrow(() -> new EntityNotFoundException("회원을 찾을 수 없습니다."));
        Member otherMember = memberRepository.findById(otherMemberId).orElseThrow(() -> new EntityNotFoundException("회원을 찾을 수 없습니다."));
        Optional<ChatRoom> chatRooms = chatParticipantRepository.findExistingPrivateRoomId(member.getId(), otherMemberId);

//        나와 상대방이 1:1 채팅에 이미 참석하고있다면 해당 roomId return
        if(chatRooms.isPresent()){
            return chatRooms.get().getId();
        }
//        만약에 1:1 채팅방이 없을 경우 기존 채팅방 개설
        ChatRoom newRoom = ChatRoom.builder()
                .name(member.getName() + "-" + otherMember.getName())
                .isGroupChat("N")
                .build();
        chatRoomRepository.save(newRoom);

//        두 사람 모두 참여자로 추가
        addParticipantToRoom(newRoom, member);
        addParticipantToRoom(newRoom, otherMember);

        return newRoom.getId();

    }
}
