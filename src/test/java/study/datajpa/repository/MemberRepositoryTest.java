package study.datajpa.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;
import study.datajpa.entity.Team;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
@Rollback(false)
class MemberRepositoryTest {

    @Autowired MemberRepository memberRepository;
    @Autowired TeamRepository teamRepository;
    @PersistenceContext EntityManager em;

    @Test
    public void testMember() {
        Member member = new Member("memberA");
        // JpaRepository의 상위 인터페이스에서 제공을한다.
        // -> 기본 CRUD, Paging 기능 제공
        Member savedMember = memberRepository.save(member);

        Member findMember = memberRepository.findById(savedMember.getId()).get();

        assertThat(findMember.getId()).isEqualTo(member.getId());
        assertThat(findMember.getUsername()).isEqualTo(member.getUsername());
        assertThat(findMember).isEqualTo(member);
    }

    @Test
    public void basicCRUD() {
        Member member1 = new Member("member1");
        Member member2 = new Member("member2");

        memberRepository.save(member1);
        memberRepository.save(member2);

        // 단건 조회 검증
        Member findMember1 = memberRepository.findById(member1.getId()).get();
        Member findMember2 = memberRepository.findById(member2.getId()).get();

        assertThat(findMember1).isEqualTo(member1);
        assertThat(findMember2).isEqualTo(member2);

        // 엔티티의 수정은 JPQL을 사용하는것이 아닌 변경감지를 활용하는것이 베스트 프렉티스이다.
        findMember1.setUsername("dirty checking");

        // 리스트 조회 검증
        List<Member> all = memberRepository.findAll();
        assertThat(all.size()).isEqualTo(2);

        // 카운트 검증
        long count = memberRepository.count();
        assertThat(count).isEqualTo(2);

        // 삭제 검증
        memberRepository.delete(member1);
        memberRepository.delete(member2);

        count = memberRepository.count();
        assertThat(count).isEqualTo(0);
    }

    @Test
    public void findByUsernameAndAgeGreaterThen() {
        Member member1 = new Member("AAA", 10);
        Member member2 = new Member("AAA", 20);
        memberRepository.save(member1);
        memberRepository.save(member2);

        List<Member> result = memberRepository.findByUsernameAndAgeGreaterThan("AAA", 15);

        assertThat(result.get(0).getUsername()).isEqualTo("AAA");
        assertThat(result.get(0).getAge()).isEqualTo(20);
        assertThat(result.size()).isEqualTo(1);
    }

    @Test
    public void testNamedQuery() {
        Member member1 = new Member("AAA", 10);
        Member member2 = new Member("BBB", 20);
        memberRepository.save(member1);
        memberRepository.save(member2);

        List<Member> members = memberRepository.findByUsername("AAA");

        assertThat(members.size()).isEqualTo(1);
    }

    @Test
    public void testQuery() {
        Member member1 = new Member("AAA", 10);
        Member member2 = new Member("BBB", 20);
        memberRepository.save(member1);
        memberRepository.save(member2);

        List<Member> members = memberRepository.findUser("AAA", 10);

        assertThat(members.size()).isEqualTo(1);
    }

    @Test
    public void testUsernameList() {
        Member member1 = new Member("AAA", 10);
        Member member2 = new Member("BBB", 20);
        memberRepository.save(member1);
        memberRepository.save(member2);

        List<String> usernames = memberRepository.findUsernames();

        for (String username : usernames) {
            System.out.println("username = " + username);
        }
    }

    @Test
    public void testUserDto() {
        Team team = new Team("teamA");
        teamRepository.save(team);

        Member member1 = new Member("AAA", 10);
        Member member2 = new Member("BBB", 20);
        member1.setTeam(team);
        memberRepository.save(member1);
        memberRepository.save(member2);

        List<MemberDto> memberDto = memberRepository.findMemberDto();
        for (MemberDto dto : memberDto) {
            System.out.println("dto = " + dto);
        }

    }

    @Test
    public void findByNames() {
        Member member1 = new Member("AAA", 10);
        Member member2 = new Member("BBB", 20);
        memberRepository.save(member1);
        memberRepository.save(member2);

        List<Member> members = memberRepository.findByNames(Arrays.asList("AAA", "BBB"));
        for (Member member : members) {
            System.out.println("member = " + member);
        }

    }

    @Test
    public void returnType() {
        Member member1 = new Member("AAA", 10);
        Member member2 = new Member("BBB", 20);
        memberRepository.save(member1);
        memberRepository.save(member2);

        /*
            컬렉션
            -> 컬렉션 결과가 없더라도 빈 컬렉션을 반환해준다.
        * */
        List<Member> members = memberRepository.findListByUsername("AAA");

        /*
            단건
            -> JPA에서는 단건 조회 결과가 없을 경우 NoResultException 예외가 발생한다.
            Spring data JPA 에서는 예외없이 null을 반환한다.
            JAVA 8 부터는 Optional을 사용
        * */
        Member member = memberRepository.findOneByUsername("BBB");

        /*
            단건 조회인데 둘이상일 경우 (Optional 상관 없이)
            -> IncorretResultSizeDateAccessException 이 발생 (Spring 예외)
            원래는 NonUniqueResultException 이 발생함 (기존 예외)
            Spring data JPA가 IncorretResultSizeDateAccessException 로 변환해 주는것
            * 예외를 공통된 예외로 변환해주어 데이터베이스 등이 변경되어도 동일한 예외 처리가 가능하게끔 한다.
        */
        Optional<Member> optionalMember = memberRepository.findOptionalByUsername("BBB");
    }

    @Test
    public void paging() {
        // given
        memberRepository.save(new Member("member1", 10));
        memberRepository.save(new Member("member2", 10));
        memberRepository.save(new Member("member3", 10));
        memberRepository.save(new Member("member4", 10));
        memberRepository.save(new Member("member5", 10));
        memberRepository.save(new Member("member6", 10));
        memberRepository.save(new Member("member7", 10));

        // when
        int age = 10;

        // Pageable 은 0 부터 페이지가 시작
        // PageRequest는 Pageable의 구현체
        PageRequest pageRequest = PageRequest.of(0, 3, Sort.by(Sort.Direction.DESC, "username"));

        Page<Member> page = memberRepository.findByAge(age, pageRequest);
        // 반환타입이 Page일 경우 totalCount 쿼리까지 같이 나간다.
        // 반환타입 List는 엔티티 목록만 조회한다.
//        long totalCount = memberRepository.totalCount(age);
        List<Member> members = page.getContent();
        long totalCount = page.getTotalElements();

        // then
        assertThat(members.size()).isEqualTo(3);
        assertThat(totalCount).isEqualTo(7);
        assertThat(page.getNumber()).isEqualTo(0); // 페이지 번호도 제공한다.
        assertThat(page.getTotalPages()).isEqualTo(3); // 총 페이지
        assertThat(page.isFirst()).isTrue(); // 첫페이지 유무
        assertThat(page.hasNext()).isTrue(); // 다음 페이지 존재 유무

        /////////////////////////////////////////

        // Slice 를 사용하면 totalCount는 가져오지 않는다.
        // Slice는 limit + 1 개를 요청한다.
        Slice<Member> slice = memberRepository.findSliceByAge(age, pageRequest);

        // > 페이징 방식을 사용하다가 더보기 방식으로 변경하게 된다면 ?
        // > 반환타입을 Slice로 수정하기만 하면 된다.

        // * 페이징을 기피하는 이유
        // totalCount 쿼리때문에 성능이 안나오는 경우가 많다.
        // > DB의 모든 데이터를 카운트해야함.
        // Spring data JPA의 페이징 기능을 활용하면 문제가 하나 존재함
        // 엔티티를 조회할때 사용한 join을 totalCount 에서도 그대로 사용하기때문에 성능상 문제가 발생함
        // totalCount 성능에 따라 다음 과 같이 최적화 진행
        // @Query(value = "select m from Member m left join m.team t", countQuery = "select count(m) from Member m")

        // * DTO로 반환하기
        // Page에서 map 메소드를 제공하기 때문에 dto로 변환하여 손쉽게 반환할 수 있음
        // Page는 API 에서 그대로 반환해도 좋음
        Page<MemberDto> toDto = page.map(member -> new MemberDto(member.getId(), member.getUsername(), null));
    }


    @Test
    public void bulkUpdate() {
        // given
        memberRepository.save(new Member("member1", 10));
        memberRepository.save(new Member("member2", 19));
        memberRepository.save(new Member("member3", 20));
        memberRepository.save(new Member("member4", 21));
        memberRepository.save(new Member("member5", 30));
        memberRepository.save(new Member("member6", 17));
        memberRepository.save(new Member("member7", 40));

        // when
        int resultCount = memberRepository.bulkAgePlus(20);
        // Spring data JPA를 사용한다면 @Modifying의 옵션으로 영속성 컨텍스트 를 비워주는 로직 생략이 가능
        em.flush(); // 변경되지 않은 부분 DB에 반영
        em.clear(); // 영속성 컨텍스트를 비워줌
        // JPA 기본 동작: JPQL을 사용하면 DB에 flush를 한번 해줌

        // JPA 를 사용하면 bulk 연산에서 조심해야 할점이다.
        // 벌크 연산은 영속성 컨텍스트가 아닌 DB에 바로 반영해버리기 때문에 영속성 컨텍스트에 존재하는 엔티티와는 다르다.
        // 벌크 연산 이후에는 영속성 컨텍스트를 날려버려야 한다.
        List<Member> result = memberRepository.findByUsername("member5");
        Member member5 = result.get(0);
        System.out.println("member5.getAge() = " + member5.getAge());
        // then
        assertThat(resultCount).isEqualTo(4);
    }

    @Test
    public void findMemberA() {
        // given

        // member1 -> teamA
        // member2 -> teamB
        // member와 team의 연관관계는 LAZY이다.
        // 실무에서는 무조건 LAZY로 지정해야한다.
        // 가짜 객체를 담아뒀다가 Team을 사용할때 Team을 조회한다.
        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");
        teamRepository.save(teamA);
        teamRepository.save(teamB);


        Member member1 = new Member("member1", 10, teamA);
        Member member2 = new Member("member2", 10, teamB);
        memberRepository.save(member1);
        memberRepository.save(member2);

        em.flush();
        em.clear();

        // when
        List<Member> members = memberRepository.findAll();

        // LAZY로 지정했기 때문에
        // N + 1 문제가 발생한다.
        // JPA에서는 이를 해결하기 위해 fetch join 기능을 제공한다.
        for (Member member : members) {
            System.out.println("member = " + member.getUsername());
            // LAZY로 지정했기 때문에 team은 프록시 객체이다
            // member.team = class study.datajpa.entity.Team$HibernateProxy$JDELlbBI
            System.out.println("member.team = " + member.getTeam().getClass());
            System.out.println("member.team = " + member.getTeam().getName());
        }

        em.flush();
        em.clear();

        System.out.println("==================");

        // fetch join을 사용하면 Member 엔티티를 조회할때 Team 엔티티까지 함께 조회해온다.
        List<Member> fetchMembers = memberRepository.findMemberFetchJoin();

        for (Member member : fetchMembers) {
            System.out.println("member = " + member.getUsername());
            System.out.println("member.team = " + member.getTeam().getClass());
            System.out.println("member.team = " + member.getTeam().getName());
        }


        // fetch join 을 사용하려면 매번 JPQL을 사용해야하기 때문에 이를 해결하기 위한 방법중 하나로
        // @EntityGraph 기능을 제공한다.

        // then
    }

    @Test
    public void queryHint() {
        // given

        // readOnly Hint를 제공한다.
        Member savedMember = memberRepository.save(new Member("member1", 10));
        Member savedMembers = memberRepository.save(new Member("members", 10));
        em.flush();
        em.clear();

        // when

        Member findMember = memberRepository.findById(savedMember.getId()).get();
        // 기존 동작이라면 영속성 컨텍스트에서 관리하는 엔티티이기 때문에 변경감지가 동작하여 update 쿼리가 날아간다.
        // 변경 감지는 내부적으로는 최적화를 하겠지만 결국 두개의 객체를 가지고 있기 때문에 메모리를 더 잡아먹는다.
        findMember.setUsername("member2");

        // 100% 조회로만 사용할것이라면 최적화하는 방식이 존재한다.
        // JPA에서는 제공하지 않고, Hibernate에서만 제공하는 기능
        // 내부적으로 snapshot을 사용하지 않기때문에 변경감지가 일어나지 않는다.
        Member findMember2 = memberRepository.findReadOnlyByUsername("members");
        findMember2.setUsername("members2");

        // 이러한 쿼리힌트를 사용하는것은 극소수이다.
        // 적용하기 전에 성능테스트를 먼저 진행해보고 결정할것.
        // 무조건 다 넣는다고 좋은것은 아님.
        // 정말 성능 최적화가 필요하다면 이미 캐시를 사용해야하기 떄문에 Redis등이 존재할것임

        // then
    }

    @Test
    public void lock() {
        // given

        Member savedMember = memberRepository.save(new Member("member1", 10));
        Member savedMembers = memberRepository.save(new Member("members", 10));
        em.flush();
        em.clear();

        // when

        // select 쿼리 후미에 for update가 사용됨
        /*
            select
                member0_.member_id as member_i1_0_,
                member0_.age as age2_0_,
                member0_.team_id as team_id4_0_,
                member0_.username as username3_0_
            from
                member member0_
            where
                member0_.username=? for update
         */
        Member member1 = memberRepository.findLockByUsername("member1");
    }

    @Test
    public void callCustom() {
        List<Member> members = memberRepository.findMemberCustom();
    }

    @Test
    public void specBasic() {
        // given
        Team teamA = new Team("teamA");
        em.persist(teamA);

        Member m1 = new Member("m1", 0, teamA);
        Member m2 = new Member("m2", 0, teamA);
        em.persist(m1);
        em.persist(m2);

        em.flush();
        em.clear();

        // when

        // Specification의 장점 기존에 정의해둔 스팩을 and, or등을 사용해서 조합이 가능하다.
        // Criteria를 활용한 기술
        // 실무에서 사용하기엔 너무 에로사항이 많다..
        // QueryDSL을 쓰도록 하자.
        Specification<Member> spec = MemberSpec.username("m1").and(MemberSpec.teamName("teamA"));
        List<Member> members = memberRepository.findAll(spec);

        // then
        assertThat(members.size()).isEqualTo(1);
    }
}










