package study.datajpa.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import study.datajpa.entity.Member;

import static org.assertj.core.api.Assertions.assertThat;

// Junit 5 부터는 @RunWith 애노테이션이 더이상 필요없다.
@SpringBootTest
@Transactional
@Rollback(false)
class MemberJpaRepositoryTest {

    @Autowired MemberJpaRepository memberJpaRepository;

    /**
     * No EntityManager with actual transaction available for current thread - cannot reliably process 'persist' call 예외 발생
     * JPA의 모든 데이터조작은 트랜잭션 내에서 이루어 져야함
     */
    @Test
    public void testMember() {
        // JPA 특성상 동일 트랜잭션 내에서는 같은 식별자를 가진 엔티티는 동일성을 보장한다.
        Member member = new Member("memberA");
        Member savedMember = memberJpaRepository.save(member);

        Member findMember = memberJpaRepository.find(savedMember.getId());

        assertThat(findMember.getId()).isEqualTo(member.getId());
        assertThat(findMember.getUsername()).isEqualTo(member.getUsername());
        assertThat(findMember).isEqualTo(member);
    }
}