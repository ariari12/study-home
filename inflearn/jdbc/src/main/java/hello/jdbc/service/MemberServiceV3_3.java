package hello.jdbc.service;

import hello.jdbc.domain.Member;
import hello.jdbc.repository.MemberRepositoryV3;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * 트랜잭션 - 트랜잭션 템플릿
 */
@Slf4j
public class MemberServiceV3_3 {


    private final MemberRepositoryV3 memberRepository;

    public MemberServiceV3_3(MemberRepositoryV3 memberRepository) {
        this.memberRepository = memberRepository;
    }

    @Transactional
    public void accountTransfer(String fromId, String toId, int money) throws SQLException {
        // 비즈니스 로직
        bizLogic(toId, money, fromId);
    }

    private void bizLogic(String toId, int money, String fromId) throws SQLException {
        Member fromMember = memberRepository.findById(fromId);
        Member tomember = memberRepository.findById(toId);

        memberRepository.update(fromId, fromMember.getMoney() - money);
        validation(tomember);
        memberRepository.update(toId, fromMember.getMoney() + money);
    }

    private static void validation(Member tomember) {
        if (tomember.getMemberId().equals("ex")){
            throw new IllegalStateException("이체중 예외 발생");
        }
    }
}
