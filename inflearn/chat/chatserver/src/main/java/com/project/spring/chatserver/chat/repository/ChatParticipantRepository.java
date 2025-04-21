package com.project.spring.chatserver.chat.repository;

import com.project.spring.chatserver.chat.domain.ChatParticipant;
import com.project.spring.chatserver.chat.domain.ChatRoom;
import com.project.spring.chatserver.member.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChatParticipantRepository extends JpaRepository<ChatParticipant, Long> {

    List<ChatParticipant> findByChatRoom(ChatRoom chatRoom);

    Optional<ChatParticipant> findByChatRoomAndMember(ChatRoom chatRoom, Member member);

    boolean existsChatParticipantByChatRoomAndMember(ChatRoom chatRoom, Member member);

    List<ChatParticipant> member(Member member);

    List<ChatParticipant> findAllByMember(Member member);

    long countByChatRoom(ChatRoom chatRoom);

    @Query("SELECT cp1.chatRoom " +
            "FROM ChatParticipant AS cp1 " +
            "JOIN ChatParticipant AS cp2 " +
            "ON cp1.chatRoom.id = cp2.chatRoom.id " +
            "WHERE cp1.member.id = :myId AND cp2.member.id = :otherMemberId " +
            "AND cp1.chatRoom.isGroupChat = 'N'")
    Optional<ChatRoom> findExistingPrivateRoomId(@Param("myId") Long myId, @Param("otherMemberId") Long otherMemberId);
}
