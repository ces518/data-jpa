package study.datajpa.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import study.datajpa.entity.Member;

/**
 * Created by IntelliJ IDEA.
 * User: june
 * Date: 2020-02-28
 * Time: 16:09
 **/
@Getter @Setter
@ToString
public class MemberDto {

    private Long id;
    private String username;
    private String teamName;

    public MemberDto(Long id, String username, String teamName) {
        this.id = id;
        this.username = username;
        this.teamName = teamName;
    }

    /**
     * DTO -> 엔티티를 바라보는것은 상관이 없음
     * 반대로 엔티티 -> DTO를 받는 방식은 사용해선 안됨
     * 엔티티는 애플리케이션 내부에서 여러군데에서 사용되기때문에 큰 문제가 되지 않지만
     * DTO같은 경우 화면에 종속적이기 떄문에 엔티티 -> DTO 방식은 사용해선 안된다.
     * @param member
     */
    public MemberDto(Member member) {
        this.id = member.getId();
        this.username = member.getUsername();
        this.teamName = member.getTeam().getName();
    }
}
