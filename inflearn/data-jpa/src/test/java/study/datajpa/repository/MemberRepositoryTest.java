package study.datajpa.repository;

import jakarta.persistence.EntityManager;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;
import study.datajpa.entity.Team;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
@Transactional
@Rollback(value = false)
class MemberRepositoryTest {
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private TeamRepository teamRepository;
    @Autowired
    private EntityManager em;
    @Test
    public void testMember(){
        Member member = new Member("memberA");
        Member savedMember = memberRepository.save(member);
        Optional<Member> byId = memberRepository.findById(savedMember.getId());
        Member findMember = byId.get();

        assertThat(member.getId()).isEqualTo(findMember.getId());
        assertThat(member.getUsername()).isEqualTo(findMember.getUsername());
        assertThat(member).isEqualTo(findMember);

    }

    @Test
    public void basicCRUD(){
        Member memberA = new Member("memberA");
        Member memberB = new Member("memberB");
        memberRepository.save(memberA);
        memberRepository.save(memberB);

        Member findMember1 = memberRepository.findById(memberA.getId()).get();
        Member findMember2 = memberRepository.findById(memberB.getId()).get();

        assertThat(memberA).isEqualTo(findMember1);
        assertThat(memberB).isEqualTo(findMember2);

        List<Member> all = memberRepository.findAll();
        assertThat(all.size()).isEqualTo(2);

        Long count = memberRepository.count();
        assertThat(count).isEqualTo(2);
    }

    @Test
    public void findByUsernameAndAgeGreaterThen(){
        Member m1 = new Member("AAA", 20);
        Member m2 = new Member("BBB", 10);
        memberRepository.save(m1);
        memberRepository.save(m2);

        List<Member> result = memberRepository.findByUsernameAndAgeGreaterThan("AAA", 15);
        assertThat(result.get(0).getUsername()).isEqualTo("AAA");
        assertThat(result.get(0).getAge()).isEqualTo(20);
        assertThat(result.size()).isEqualTo(1);

    }

    @Test
    public void testQuery(){
        Member m1 = new Member("AAA", 20);
        Member m2 = new Member("BBB", 10);
        memberRepository.save(m1);
        memberRepository.save(m2);

        List<Member> findMember = memberRepository.findUser("AAA", 20);

        assertThat(m1).isEqualTo(findMember.get(0));
    }

    @Test
    public void testFindUsername(){
        Member m1 = new Member("AAA", 20);
        Member m2 = new Member("BBB", 10);
        memberRepository.save(m1);
        memberRepository.save(m2);

        for (String s : memberRepository.findUserNameList()) {
            System.out.println("s="+s);
        }
    }

    @Test
    public void testFindMemberDto(){
        Team teamA = new Team("teamA");
        teamRepository.save(teamA);
        Member m1 = new Member("AAA", 20);
        m1.setTeam(teamA);
        memberRepository.save(m1);


        for (MemberDto memberDto : memberRepository.findMemberDto()) {
            System.out.println(memberDto);
        }
    }
    @Test
    public void findByNames(){
        Member m1 = new Member("AAA", 20);
        Member m2 = new Member("BBB", 10);
        memberRepository.save(m1);
        memberRepository.save(m2);

        List<Member> byNames = memberRepository.findByNames(Arrays.asList("AAA", "BBB"));
        byNames.forEach(s-> System.out.println("byNames = " + s));

    }
    @Test
    public void paging(){
        memberRepository.save(new Member("member1", 10));
        memberRepository.save(new Member("member2", 10));
        memberRepository.save(new Member("member3", 10));
        memberRepository.save(new Member("member4", 10));
        memberRepository.save(new Member("member5", 10));

        int age=10;
        PageRequest pageRequest = PageRequest.of(0, 3, Sort.Direction.DESC,"username");

        Page<Member> page = memberRepository.findByAge(age, pageRequest);
        Page<MemberDto> map = page.map(m -> new MemberDto(m.getId(), m.getUsername(), null));
        List<Member> content = page.getContent();

        assertThat(content.size()).isEqualTo(3);
        assertThat(page.getTotalElements()).isEqualTo(5);
        assertThat(page.getNumber()).isEqualTo(0);
        assertThat(page.getTotalPages()).isEqualTo(2);
        assertThat(page.isFirst()).isEqualTo(true);
        assertThat(page.hasNext()).isEqualTo(true);

        page.forEach(System.out::println);
        content.forEach(System.out::println);

    }
    @Test
    public void bulkUpdate(){
        memberRepository.save(new Member("member1", 10));
        memberRepository.save(new Member("member2", 19));
        memberRepository.save(new Member("member3", 20));
        memberRepository.save(new Member("member4", 21));
        memberRepository.save(new Member("member5", 40));

        int members = memberRepository.bulkAgePlus(20);

        Member member = memberRepository.findListByUsername("member4").get(0);

        System.out.println(member);
        assertThat(members).isEqualTo(3);
    }
    @Test
    public void findMemberLazy(){
        Team teamA = teamRepository.save(new Team("teamA"));
        Team teamB = teamRepository.save(new Team("teamB"));
        Member memberA = new Member("memberA");
        Member memberB = new Member("memberB");
        memberA.changeTeam(teamA);
        memberB.changeTeam(teamB);
        memberRepository.save(memberA);
        memberRepository.save(memberB);
        em.flush();
        em.clear();


        memberRepository.findEntityGraphByUsername("memberA").forEach(member ->
        {
            System.out.println("member = " + member);
            System.out.println("team = "+member.getTeam().getClass());
            System.out.println("team = "+member.getTeam().getName());
        });
    }
    @Test
    public void queryHint(){
        memberRepository.save(new Member("member1",10));
        em.flush();
        em.clear();

        Member findMember = memberRepository.findReadOnlyByUsername("member1");
        findMember.setUsername("member2");
        em.flush();
    }

    @Test
    public void lock(){
        memberRepository.save(new Member("member1",10));
        em.flush();
        em.clear();

        List<Member> result = memberRepository.findLockByUsername("member1");
        em.flush();
    }

    @Test
    public void callCustom(){
        List<Member> memberCustom = memberRepository.findMemberCustom();
    }

}