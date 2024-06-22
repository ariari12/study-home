package hello.core.java.member;

public interface MemberService {
    void join(Member member);
    Member findMember(Long memberId);
}
