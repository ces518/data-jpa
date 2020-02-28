package study.datajpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import study.datajpa.entity.Member;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: june
 * Date: 2020-02-23
 * Time: 23:12
 **/
public interface MemberRepository extends JpaRepository<Member, Long> {

    // JPQL에서 명확하게 namedParam을 사용했을때 @Param을 사용해야한다.
    // 애노테이션이 없어도 동작한다.
    // -> 먼저 엔티티명.메서드명의 NamedQuery가 존재하는지 먼저 찾고, 없다면 메서드명으로 쿼리를 생성한다.
    // 따라서 아래의 애노테이션은 생략이 가능하다.
//    @Query(name = "Member.findByUsername")
    List<Member> findByUsername(@Param("username") String username);

    List<Member> findByUsernameAndAgeGreaterThan(String username, int age);
}
