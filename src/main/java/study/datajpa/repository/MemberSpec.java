package study.datajpa.repository;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;
import study.datajpa.entity.Member;
import study.datajpa.entity.Team;

import javax.persistence.criteria.*;

/**
 * Created by IntelliJ IDEA.
 * User: june
 * Date: 2020-03-11
 * Time: 01:49
 **/
public class MemberSpec {

    public static Specification<Member> teamName(final String teamName) {

        return (Specification<Member>) (root, query, criteriaBuilder) -> {

            if (StringUtils.isEmpty(teamName)) {
                return null;
            }

            Join<Member, Team> team = root.join("team", JoinType.INNER);// 회원과 팀 조인
            return criteriaBuilder.equal(team.get("name"), teamName);
        };
    }

    public static Specification<Member> username(final String username) {
        return (Specification<Member>) (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("username"), username);
    }
}
