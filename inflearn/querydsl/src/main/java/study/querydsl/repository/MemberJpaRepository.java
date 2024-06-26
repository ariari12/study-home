package study.querydsl.repository;

import com.querydsl.core.BooleanBuilder;

import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Repository;

import study.querydsl.dto.MemberSearchCondition;
import study.querydsl.dto.MemberTeamDto;
import study.querydsl.dto.QMemberTeamDto;
import study.querydsl.entity.Member;


import java.util.List;
import java.util.Optional;

import static org.springframework.util.StringUtils.*;
import static study.querydsl.entity.QMember.*;
import static study.querydsl.entity.QTeam.team;

@Repository
public class MemberJpaRepository {
    private final EntityManager em;
    private final JPAQueryFactory queryFactory;

    public MemberJpaRepository(EntityManager em,JPAQueryFactory queryFactory) {
        this.em = em;
        //this.queryFactory = new JPAQueryFactory(em); 생성자에서 직접 생성해도 되고
        this.queryFactory=queryFactory; //이렇게 Bean으로 등록해서 자동주입으로 사용해도 됨 취향
    }

    public void save(Member member){
        em.persist(member);
    }
    public Optional<Member> findById(Long id){
        return Optional.ofNullable(em.find(Member.class, id));
    }
    public List<Member> findAll(){
        return em.createQuery("select m from Member m",Member.class).getResultList();
    }

    public List<Member> findAll_QueryDsl(){
        return queryFactory.selectFrom(member).fetch();
    }

    public List<Member> findByUsername(String username){
        return em.createQuery("select m from Member m where m.username=:username",Member.class)
                .setParameter("username",username)
                .getResultList();
    }
    public List<Member> findByUsername_QueryDsl(String username){
        return queryFactory.selectFrom(member)
                .where(member.username.eq(username))
                .fetch();
    }

    public List<MemberTeamDto> searchByBuilder(MemberSearchCondition condition){
        BooleanBuilder builder = new BooleanBuilder(); // 초기조건문 넣는게 가능
        if (hasText(condition.getUsername())) {
            builder.and(member.username.eq(condition.getUsername()));
        }
        if (hasText(condition.getTeamName())) {
            builder.and(team.name.eq(condition.getTeamName()));
        }
        if (condition.getAgeGoe() != null) {
            builder.and(member.age.goe(condition.getAgeGoe()));
        }
        if (condition.getAgeLoe() != null) {
            builder.and(member.age.loe(condition.getAgeLoe()));
        }


        return queryFactory.select(
                new QMemberTeamDto(
                        member.id.as("memberId"),
                        member.username,
                        member.age,
                        team.id.as("teamId"),
                        team.name.as("teamName")))
                .from(member)
                .where(builder)
                .leftJoin(member.team, team)
                .fetch();
    }

    public List<MemberTeamDto> search(MemberSearchCondition condition){
        return queryFactory.select(
                        new QMemberTeamDto(
                                member.id.as("memberId"),
                                member.username,
                                member.age,
                                team.id.as("teamId"),
                                team.name.as("teamName")))
                .from(member)
                .where(usernameEq(condition.getUsername()),
                        teamNameEq(condition.getTeamName()),
                        ageGoe(condition.getAgeGoe()),
                        ageLoe(condition.getAgeLoe()),
                        ageBetween(condition.getAgeLoe(),condition.getAgeGoe()))
                .leftJoin(member.team, team)
                .fetch();

    }

    private BooleanExpression ageBetween(Integer ageLoe, Integer ageGoe) {
        return ageLoe==null || ageGoe==null? null :ageGoe(ageLoe).and(ageLoe(ageGoe)); //null 값체크는 강의에없는 내용 틀릴수도있음
    }

    private BooleanExpression usernameEq(String username) {
        return hasText(username)? member.username.eq(username) : null ;
    }
    private BooleanExpression teamNameEq(String teamName) {
        return hasText(teamName)? team.name.eq(teamName) : null ;
    }
    private BooleanExpression ageGoe(Integer ageGoe) {
        return ageGoe == null ?  null : member.age.goe(ageGoe) ;
    }
    private BooleanExpression ageLoe(Integer ageLoe) {
        return ageLoe == null ?  null : member.age.goe(ageLoe) ;
    }
}
