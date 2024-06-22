package hello.core.java.member;

public interface MemberRepository {
    void save(Member member);
    Member findById(Long memberID);
}
