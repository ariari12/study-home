package study.querydsl.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class MemberDto {
    private String username;
    private int age;

    @QueryProjection //gradle 에서 compileJava 실행
    public MemberDto(String username, int age) {
        this.username = username;
        this.age = age;
    }
}
