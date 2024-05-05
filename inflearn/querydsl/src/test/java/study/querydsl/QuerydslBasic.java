package study.querydsl;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.Wildcard;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.annotation.Commit;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import study.querydsl.dto.MemberDto;
import study.querydsl.dto.QMemberDto;
import study.querydsl.dto.UserDto;
import study.querydsl.entity.Member;
import study.querydsl.entity.QMember;
import study.querydsl.entity.QTeam;
import study.querydsl.entity.Team;

import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static study.querydsl.entity.QMember.*;
import static study.querydsl.entity.QTeam.*;

@SpringBootTest
@Transactional
public class QuerydslBasic {
    @PersistenceContext
    EntityManager em;
    JPAQueryFactory queryFactory;

    @BeforeEach
    public void before() {
        queryFactory = new JPAQueryFactory(em);
        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");
        em.persist(teamA);
        em.persist(teamB);

        Member member1 = new Member("member1", 10, teamA);
        Member member2 = new Member("member2", 20, teamA);
        Member member3 = new Member("member3", 30, teamB);
        Member member4 = new Member("member4", 40, teamB);

        em.persist(member1);
        em.persist(member2);
        em.persist(member3);
        em.persist(member4);
    }

    @Test
    public void startJPQL() {
        //member1을 찾아라
        Member findMember = em.createQuery("select m from Member m where m.username = :username", Member.class)
                .setParameter("username", "member1")
                .getSingleResult();
        assertThat(findMember.getUsername()).isEqualTo("member1");
    }

    @Test
    public void startQuerydsl() {
        //JPAQueryFactory queryFactory = new JPAQueryFactory(em); 필드로 뺄 수 있음
        //QMember m = new QMember("m");  스태틱으로 뺴버릴 수 있음

        Member findMember = queryFactory
                .select(member)
                .from(member)
                .where(member.username.eq("member1"))
                .fetchOne();
        assertThat(findMember.getUsername()).isEqualTo("member1");

    }

    @Test
    public void search() {
        Member member1 = queryFactory
                .selectFrom(member)
                .where(member.username.eq("member1")
                        .and(member.age.eq(10)))
                .fetchOne();
        assertThat(member1.getUsername()).isEqualTo("member1");
        assertThat(member1.getAge()).isEqualTo(10);
    }

    @Test
    public void searchAndParam() {
        Member member1 = queryFactory
                .selectFrom(member)
                .where(member.username.eq("member1"), // and를 , 로 대체가능
                        (member.age.eq(10)))
                .fetchOne();
        assertThat(member1.getUsername()).isEqualTo("member1");
        assertThat(member1.getAge()).isEqualTo(10);
    }

    @Test
    public void resultFetch() {
        //list
        List<Member> memberList = queryFactory.selectFrom(member).fetch();
        //fetchOne
        Member fetchOne = queryFactory.selectFrom(member).fetchOne();
        //fetchFirst
        Member fetchFirst = queryFactory.selectFrom(member).fetchFirst();
        //count 쿼리
        queryFactory
                //.select(Wildcard.count) //count(*) 쿼리 모든 칼럼 조회
                .select(member.count()) //count(id) 쿼리
                .from(member)
                .fetchOne();
    }

    /*
     * 회원 정렬 순서
     * 1. 회원 나이 내림차순(desc)
     * 2. 회원 이름 오름차순(asc)
     * 단 2에서 회원 이름이 없으면 마지막에 출력(nulls last)
     * */
    @Test
    public void sort() {
        em.persist(new Member(null, 100));
        em.persist(new Member("member5", 100));
        em.persist(new Member("member6", 100));

        List<Member> memberList = queryFactory
                .selectFrom(member)
                .where(member.age.eq(100))
                .orderBy(member.age.desc(), member.username.asc().nullsLast())
                .fetch();
        Member member5 = memberList.get(0);
        Member member6 = memberList.get(1);
        Member memberNull = memberList.get(2);

        assertThat(member5.getUsername()).isEqualTo("member5");
        assertThat(member6.getUsername()).isEqualTo("member6");
        assertThat(memberNull.getUsername()).isNull();


    }

    @Test
    public void paging1() {
        List<Member> memberList = queryFactory
                .selectFrom(member)
                .orderBy(member.username.desc())
                .offset(1)
                .limit(2)
                .fetch();
        assertThat(memberList.size()).isEqualTo(2);

    }

    @Test
    public void paging2() {
        List<Member> memberList = queryFactory
                .selectFrom(member)
                .orderBy(member.username.desc())
                .offset(1)
                .limit(2)
                .fetch();
        //Pageable객체를 생성할 때 limit, offset 과 현재 페이지등을 설정하면 pageable.getOffset(), pageable.getLimit()
    }

    @Test
    public void aggregation() {
        List<Tuple> tuples = queryFactory
                .select(
                        member.count(),
                        member.age.sum(),
                        member.age.avg(),
                        member.age.max(),
                        member.age.min())
                .from(member)
                .fetch();

        Tuple tuple = tuples.get(0);
        assertThat(tuple.get(member.count())).isEqualTo(4);
        assertThat(tuple.get(member.age.sum())).isEqualTo(100);
        assertThat(tuple.get(member.age.avg())).isEqualTo(25);
        assertThat(tuple.get(member.age.max())).isEqualTo(40);
        assertThat(tuple.get(member.age.min())).isEqualTo(10);
    }

    /**
     * 팀의 이름과 각 팀의 평균 연령을 구해라.
     */
    @Test
    @Rollback(value = false)
    public void group() throws Exception {
        List<Tuple> result = queryFactory.select(team.name, member.age.avg())
                .from(member)
                .join(member.team, team)
                .groupBy(team.name)
                .fetch();

        Tuple teamA = result.get(0);
        Tuple teamB = result.get(1);

        assertThat(teamA.get(team.name)).isEqualTo("teamA");
        assertThat(teamA.get(member.age.avg())).isEqualTo(15);

        assertThat(teamB.get(team.name)).isEqualTo("teamB");
        assertThat(teamB.get(member.age.avg())).isEqualTo(35);
    }

    /**
     * 팀 A에 소속된 모든 회원을 찾아라
     */
    @Test
    public void join() {
        List<Member> memberList = queryFactory.select(member)
                .from(member)
                .join(member.team, team)
                .where(team.name.eq("teamA"))
                .fetch();

        assertThat(memberList).extracting("username")
                .containsExactly("member1", "member2");
    }

    /**
     * 세타 조인(연관관계가 없는 필드로 조인)
     * left join 불가능
     * 회원의 이름이 팀 이름과 같은 회원 조회
     */
    @Test
    public void theta_join() throws Exception {
        em.persist(new Member("teamA"));
        em.persist(new Member("teamB"));
        List<Member> result = queryFactory
                .select(member)
                .from(member, team)
                .where(member.username.eq(team.name))
                .fetch();
        assertThat(result)
                .extracting("username")
                .containsExactly("teamA", "teamB");
    }

    @Test
    /**
     * 예) 회원과 팀을 조인하면서, 팀 이름이 teamA인 팀만 조인, 회원은 모두 조회
     * JPQL : select m,t from Member left join m.team t on m.team.name='teamA'
     */

    public void join_on_filtering() {
        List<Tuple> tuples = queryFactory.select(member, team)
                .from(member)
                .leftJoin(member.team, team)
                .on(team.name.eq("teamA"))
//                .where(team.name.eq("teamA")) //inner join일때는 on절과 똑같지만 left join 일때는 값이 달라짐
                .fetch();

        for (Tuple tuple : tuples) {
            System.out.println("tuple = " + tuple);
        }
    }

    /**
     * 연관관계가 없는 외부조인
     * 회원의 이름이 팀 이름과 같은 대상 외부 조인
     */
    @Test
    public void join_on_no_relation() {
        em.persist(new Member("teamA"));
        em.persist(new Member("teamB"));
        em.persist(new Member("teamC"));
        List<Tuple> tuples = queryFactory
                .select(member, team)
                .from(member)
                .leftJoin(team) //문법이 달라짐 연관관계가 없으므로 엔티티 하나만 들어감
                .on(member.username.eq(team.name))
                .fetch();

        for (Tuple tuple : tuples) {
            System.out.println("tuple = " + tuple);
        }

    }

    @PersistenceUnit
    EntityManagerFactory emf;

    @Test
    public void fetchJoin() {
        em.flush();
        em.clear();

        Member findMember = queryFactory.selectFrom(member)
                .where(member.username.eq("member1"))
                .fetchOne();

        boolean loaded = emf.getPersistenceUnitUtil().isLoaded(findMember.getTeam()); //emf에서 엔티티가 로딩되었는지 아닌지 확인용
        assertThat(loaded).as("페치 조인 미적용").isFalse();

    }

    @Test
    public void fetchJoinUse() {
        em.flush();
        em.clear();

        Member findMember = queryFactory
                .selectFrom(member)
                .join(member.team, team).fetchJoin()
                .where(member.username.eq("member1"))
                .fetchOne();

        boolean loaded = emf.getPersistenceUnitUtil().isLoaded(findMember.getTeam());
        assertThat(loaded).as("페치 적용").isTrue();

    }

    /**
     * 나이가 가장 많은 회원 조회
     */
    @Test
    public void subQuery() {
        QMember memberSub = new QMember("memberSub");
        List<Member> result = queryFactory.selectFrom(member)
                .where(member.age.eq(JPAExpressions.select(memberSub.age.max())
                        .from(memberSub))).fetch();
        assertThat(result)
                .extracting("age")
                .containsExactly(40);

    }

    /**
     * 나이가 평균 이상인 회원 조회
     */
    @Test
    public void subQueryGoe() {
        QMember memberSub = new QMember("memberSub");
        List<Member> result = queryFactory.selectFrom(member)
                .where(member.age.goe(JPAExpressions.select(memberSub.age.avg())
                        .from(memberSub))).fetch();
        assertThat(result)
                .extracting("age")
                .containsExactly(30, 40);

    }

    @Test
    public void subQueryIn() {
        QMember memberSub = new QMember("memberSub");
        List<Member> result = queryFactory.selectFrom(member)
                .where(member.age.in(JPAExpressions.select(memberSub.age)
                        .from(memberSub).where(memberSub.age.gt(10)))).fetch();
        assertThat(result)
                .extracting("age")
                .containsExactly(20, 30, 40);

    }

    @Test
    public void subQuerySelect() {
        QMember memberSub = new QMember("memberSub");
        List<Double> result = queryFactory.select(
                        JPAExpressions.select(memberSub.age.avg()).from(memberSub))
                .from(member).fetch();

        for (Double v : result) {
            System.out.println(result);
        }
    }

    @Test
    public void basicCase(){
        List<String> result = queryFactory.select(
                member.age
                        .when(10).then("열살")
                        .when(20).then("스무살")
                        .otherwise("기타")
        ).from(member).fetch();

        for (String s : result) {
            System.out.println(s);
        }
    }

    @Test
    public void complexCase(){
        List<String> result = queryFactory.select(
                new CaseBuilder()
                        .when(member.age.between(0,20)).then("0~20살")
                        .when(member.age.between(21,30)).then("21살~30살")
                        .otherwise("기타")
        ).from(member).fetch();

        for (String s : result) {
            System.out.println(s);
        }
    }
    @Test
    public void constant(){
        for (Tuple result : queryFactory.select(member.username, Expressions.constant("A"))
                .from(member)
                .fetch()) {

            System.out.println(result);
        }
    }

    @Test
    public void concat(){
        List<String> result = queryFactory
//                .select(member.username.concat("_").concat(member.age)) //concat은 문자만 더해주는게 가능 타입이 안맞음
                .select(member.username.concat("_")
                        .concat(member.age.stringValue()))//타입을 바꿔주면 가능
                .from(member)
                .where(member.username.eq("member1"))

                .fetch();

        for (String s : result) {
            System.out.println(s);
        }
    }

    @Test
    public void simpleProjection(){
        List<Integer> result = queryFactory.select(member.age).from(member)
                .fetch();

        for (Integer i : result) {
            System.out.println(i);
        }
    }

    @Test
    public void tupleProjection(){
        List<Tuple> result = queryFactory
                .select(member.username, member.age)
                .from(member)
                .fetch();

        for (Tuple tuple : result) {
            String username = tuple.get(member.username);
            Integer age = tuple.get(member.age);
            System.out.println("username="+ username);
            System.out.println("username="+ age);
        }
    }

    @Test
    public void findDtoByJpql(){
        List<MemberDto> resultList = em.createQuery("select new study.querydsl.dto.MemberDto(m.username, m.age) from Member m", MemberDto.class).getResultList();

        for (MemberDto memberDto : resultList) {
            System.out.println(memberDto);
        }
    }

    @Test
    public void findDtoBySetter(){
        List<MemberDto> result = queryFactory.select(Projections.bean(MemberDto.class, member.age, member.username))
                .from(member)
                .fetch();
        for (MemberDto memberDto : result) {
            System.out.println(memberDto);
        }
    }

    @Test
    public void findDtoByFiled(){
        List<MemberDto> result = queryFactory.select(Projections.fields(MemberDto.class, member.age, member.username))
                .from(member)
                .fetch();
        for (MemberDto memberDto : result) {
            System.out.println(memberDto);
        }
    }

    @Test
    public void findUserDto(){
        QMember memberSub = new QMember("memberSub");
        List<UserDto> result = queryFactory.select(
                //dto와 엔티티 속성 이름이 다를 때 as 별칭을 사용
                Projections.fields(UserDto.class, member.username.as("name"),
                        //서브 쿼리의 결과를 as 별칭을 정해서 같은 이름의 필드에 주입
                        Expressions.as(JPAExpressions.select(memberSub.age.max()).from(memberSub),"age"))
                )
                .from(member)
                .fetch();
        for (UserDto userDto : result) {
            System.out.println(userDto);
        }

    }

    @Test
    public void findDtoByConstructors() {
        // 생성자는 타입으로 만들어지므로 Dto 상관없음 그러므로 순서가 중요함
        List<MemberDto> result = queryFactory.select(Projections.constructor(MemberDto.class, member.username, member.age))
                .from(member)
                .fetch();
        for (MemberDto memberDto : result) {
            System.out.println(memberDto);
        }

        List<UserDto> result1 = queryFactory.select(Projections.constructor(UserDto.class,  member.username,member.age))
                .from(member)
                .fetch();
        for (UserDto userDto : result1) {
            System.out.println(userDto);
        }

    }

    @Test
    public void findDtoByQueryProjection(){
        List<MemberDto> result = queryFactory
                .select(new QMemberDto(member.username, member.age))
                .from(member)
                .fetch();

        for (MemberDto memberDto : result) {
            System.out.println(memberDto);
        }
    }
    @Test
    public void dynamicQuery_BooleanBuilder(){
        String usernameParma = "member1";
        Integer ageParam = null;
        List<Member> result=searchMember1(usernameParma, ageParam);
        assertThat(result.size()).isEqualTo(1);
    }

    private List<Member> searchMember1(String usernameCond, Integer ageCond) {
        BooleanBuilder builder = new BooleanBuilder();
        if(usernameCond !=null){
            builder.and(member.username.eq(usernameCond));
        }
        if (ageCond != null){
            builder.and(member.age.eq(ageCond));
        }
        return queryFactory.selectFrom(member)
                .where(builder)
                .fetch();
    }

    @Test
    public void dynamicQuery_WhereParam(){
        String usernameParma = "member1";
        Integer ageParam = null;

        List<Member> result=searchMember2(usernameParma, ageParam);
        assertThat(result.size()).isEqualTo(1);
    }

    private List<Member> searchMember2(String usernameCond, Integer ageCond) {
        return queryFactory.selectFrom(member)
//                .where(usernameEq(usernameCond), ageEq(ageCond))
                .where(allEq(usernameCond,ageCond))
                .fetch();
    }

    private BooleanExpression ageEq(Integer ageCond) {
        return ageCond==null?null:member.age.eq(ageCond);
    }

    private BooleanExpression usernameEq(String usernameCond) {
        if(usernameCond == null){
            return null;
        }
        return member.username.eq(usernameCond);
    }

    private BooleanExpression allEq(String userCond, Integer ageCond){
        return usernameEq(userCond).and(ageEq(ageCond));
    }

    @Test
    @Commit
    public void bulkUpdate(){
        //member1 = 비회원
        //member2 = 비회원
        //member3 = 유지
        //member4 = 유지
        long count = queryFactory.update(member)
                .set(member.username, "비회원")
                .where(member.age.lt(28))
                .execute();
        em.flush();

        List<Member> result = queryFactory.selectFrom(member).fetch();
        for (Member member1 : result) {
            System.out.println(member1);
        }

    }

    @Test
    //@Commit
    public void bulkUpdateJpql(){
        //member1 = 비회원
        //member2 = 비회원
        //member3 = 유지
        //member4 = 유지
        long count = queryFactory.update(member)
                .set(member.username, "비회원")
                .where(member.age.lt(28))
                .execute();

        List<Member> result = em.createQuery("select m from Member m",Member.class).getResultList();
        for (Member member1 : result) {
            System.out.println(member1);
        }

    }

    @Test
    public void bulkAdd(){
        long count = queryFactory.update(member)
                .set(member.age, member.age.add(1))
                .execute();
    }
    @Test
    public void bulkDelete(){
        long count = queryFactory.delete(member).where(member.age.gt(10))
                .execute();

    }
    @Test
    public void sqlFunction(){
        List<String> result = queryFactory.select(
                Expressions.stringTemplate("function('replace',{0},{1},{2})", member.username, "member", "M")
                )
                .from(member)
                .fetch();

        for (String s : result) {
            System.out.println("s = " + s);
        }
    }

    @Test
    public void sqlFunction2(){
        List<String> result = queryFactory.select(member.username)
                .from(member)
//                .where(member.username.eq(Expressions
//                        .stringTemplate("function('lower',{0})", member.username)))
                .where(member.username.eq(member.username.lower()))
                .fetch();

        for (String s : result) {
            System.out.println("s = " + s);
        }
    }
}

