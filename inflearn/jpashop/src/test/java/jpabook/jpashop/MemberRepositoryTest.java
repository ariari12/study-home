package jpabook.jpashop;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
class MemberRepositoryTest {
//    @Autowired
//    MemberRepository memberRepository;

//    @Test
//    @Transactional
//    @Rollback(value = false)
//    void testMember() {
//        Member member = new Member();
//        member.setUsername("memberA");
//
//        Long savedId = memberRepository.save(member);
//        Member findMember = memberRepository.findId(savedId);
//
//
//        assertThat(findMember.getId()).isEqualTo(savedId);
//        assertThat(member.getUsername()).isEqualTo(findMember.getUsername());
//        assertThat(member).isEqualTo(findMember);
//
//
//
//    }

}