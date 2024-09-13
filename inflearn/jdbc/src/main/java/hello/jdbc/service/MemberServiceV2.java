package hello.jdbc.service;

import hello.jdbc.domain.Member;
import hello.jdbc.repository.MemberRepositoryV2;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * 트랜잭션 - 파라미터 연동, 풀을 고려한 종료
 */
@Slf4j
@RequiredArgsConstructor
public class MemberServiceV2 {
    private final DataSource dataSource;
    private final MemberRepositoryV2 memberRepository;

    public void accountTransfer(String fromId, String toId, int money) throws SQLException {

        Connection con = dataSource.getConnection();
        try{
            con.setAutoCommit(false); // 트랙잭션 시작
            // 비즈니스 로직
            bizLogic(con, toId, money, fromId);
            con.commit();

        }catch (Exception e){
            con.rollback(); // 실패시 롤백
            throw new IllegalStateException(e);
        }finally {
            release(con);
        }


    }

    private void bizLogic(Connection con, String toId, int money, String fromId) throws SQLException {
        Member fromMember = memberRepository.findById(con, fromId);
        Member tomember = memberRepository.findById(con, toId);

        memberRepository.update(con, fromId, fromMember.getMoney() - money);
        validation(tomember);
        memberRepository.update(con, toId, fromMember.getMoney() + money);
    }

    private static void release(Connection con) {
        if(con != null){
            try{
                con.setAutoCommit(true); //커넥션 풀 고려
                con.close();
            }catch(Exception e){
                log.info("error",e);
            }
        }
    }

    private static void validation(Member tomember) {
        if (tomember.getMemberId().equals("ex")){
            throw new IllegalStateException("이체중 예외 발생");
        }
    }
}
