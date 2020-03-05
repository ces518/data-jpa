package study.datajpa.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;
import study.datajpa.entity.Team;

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
}