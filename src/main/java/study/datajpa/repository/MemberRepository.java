package study.datajpa.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;

import javax.persistence.LockModeType;
import javax.persistence.QueryHint;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * Created by IntelliJ IDEA.
 * User: june
 * Date: 2020-02-23
 * Time: 23:12
 **/
public interface MemberRepository extends JpaRepository<Member, Long>, MemberRepositoryCustom, JpaSpecificationExecutor<Member> {

    // JPQL에서 명확하게 namedParam을 사용했을때 @Param을 사용해야한다.
    // 애노테이션이 없어도 동작한다.
    // -> 먼저 엔티티명.메서드명의 NamedQuery가 존재하는지 먼저 찾고, 없다면 메서드명으로 쿼리를 생성한다.
    // 따라서 아래의 애노테이션은 생략이 가능하다.
//    @Query(name = "Member.findByUsername")
    List<Member> findByUsername(@Param("username") String username);

    List<Member> findByUsernameAndAgeGreaterThan(String username, int age);

    @Query("select m from Member m where m.username = :username and m.age = :age")
    List<Member> findUser(@Param("username") String username, @Param("age") int age);

    @Query("select m.username from Member m")
    List<String> findUsernames();

    // DTO 조회시 new operation을 사용해야 한다.
    @Query("select new study.datajpa.dto.MemberDto(m.id, m.username, t.name) from Member m join m.team t")
    List<MemberDto> findMemberDto();

    @Query("select m from Member m where m.username in :names")
    List<Member> findByNames(@Param("names") Collection<String> names);

    List<Member> findListByUsername(String username); // 컬렉션

    Member findOneByUsername(String username); // 단건

    Optional<Member> findOptionalByUsername(String username); // optional

    Page<Member> findByAge(int age, Pageable pageable);

    @Query(value = "select m from Member m left join m.team t", countQuery = "select count(m) from Member m")
    Slice<Member> findSliceByAge(int age, Pageable pageable);

    // @Modifying 애노테이션이 있어야 update쿼리를 실행한다.
    // 데이터 변경이 일어나는 쿼리는 @Modifying 애노테이션을 사용할것.
    // 만약 @Modifying애노테이션이 없다면 예외가 발생한다.
    @Modifying(clearAutomatically = true) // 영속성 컨텍스트를 자동으로 비워주는 옵션
    @Query("update Member m set m.age = m.age + 1 where m.age >= :age")
    int bulkAgePlus(@Param("age") int age);

    @Query("select m from Member m left join fetch m.team")
    List<Member> findMemberFetchJoin();

    /*
        @EntityGraph
        - fetch join을 간단하게 사용하고 싶을때 사용
        - attributePaths 에 해당 속성을 지정해주면 된다.
        아래의 세가지 방식을 지원한다.

        > EntityGraph는 JPA 2.2 부터 제공하는 기능이다.
        -> NamedEntityGraph 라는 기능도 존재함
        -> NamedQuery와 유사하다.

        * 간단한 fetch join의 경우 EntityGraph를 사용하고 복잡해 지는경우 JPQL 혹은 QueryDSL 사용
    */
    @Override
    @EntityGraph(attributePaths = "team")
    List<Member> findAll();

    @EntityGraph(attributePaths = "team")
    @Query("select m from Member m")
    List<Member> findMemberEntityGraph();

//    @EntityGraph(attributePaths = "team")
    @EntityGraph("Member.All") // NamedEntityGraph 기능 사용
    List<Member> findEntityGraphByUsername(@Param("name") String username);

    @QueryHints(value = @QueryHint(name = "org.hibernate.readOnly", value = "true"))
    Member findReadOnlyByUsername(@Param("name") String username);

    // select from update
    // DB에서 셀렉 할때 Lock을 거는 방식을 JPA에서도 지원한다.
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Member findLockByUsername(@Param("name") String username);
}
