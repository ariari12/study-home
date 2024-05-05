package study.querydsl.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import study.querydsl.dto.MemberSearchCondition;
import study.querydsl.dto.MemberTeamDto;
import study.querydsl.dto.QMemberTeamDto;

import java.util.List;

import static org.springframework.util.StringUtils.hasText;
import static study.querydsl.entity.QMember.member;
import static study.querydsl.entity.QTeam.team;

//사용자 정의 구현 클래스 이름은 'springdateJPA 인터페이스 이름 + Impl' 을 꼭 적어야함
public class MemberRepositoryImpl implements MemberRepositoryCustom{
    private final JPAQueryFactory queryFactory;
    public MemberRepositoryImpl(JPAQueryFactory queryFactory){
        this.queryFactory =queryFactory;

    }
    @Override
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
                        ageLoe(condition.getAgeLoe())
                        )
                .leftJoin(member.team, team)
                .fetch();

    }

    @Override
    public Page<MemberTeamDto> searchPage(MemberSearchCondition condition, Pageable pageable) {
        List<MemberTeamDto> content = queryFactory.select(
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
                        ageLoe(condition.getAgeLoe())
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .leftJoin(member.team, team)
                .fetch();


//        Long total = queryFactory
//                .select(member.count())
//                .from(member)
//                .where()
//                .where(usernameEq(condition.getUsername()),
//                        teamNameEq(condition.getTeamName()),
//                        ageGoe(condition.getAgeGoe()),
//                        ageLoe(condition.getAgeLoe())
//                )
//                .leftJoin(member.team, team)
//                .fetchOne();
//
//        return new PageImpl<>(content,pageable,total);

        // count 쿼리를 최적화
        JPAQuery<Long> countQuery = queryFactory
                .select(member.count())
                .from(member)
                .where()
                .where(usernameEq(condition.getUsername()),
                        teamNameEq(condition.getTeamName()),
                        ageGoe(condition.getAgeGoe()),
                        ageLoe(condition.getAgeLoe())
                )
                .leftJoin(member.team, team);
        // 카운트 쿼리를 생략해주는 utils 생략하는 경우가 "마지막 페이지이면서 컨텐츠 사이즈가 페이지 사이즈보다 작을 때"
        return PageableExecutionUtils.getPage(content,pageable, countQuery::fetchOne);
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
