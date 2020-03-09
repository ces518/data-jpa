package study.datajpa.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;
import study.datajpa.repository.MemberRepository;

import javax.annotation.PostConstruct;
import java.util.Optional;

/**
 * Created by IntelliJ IDEA.
 * User: june
 * Date: 2020-03-08
 * Time: 20:15
 **/
@RestController
@RequiredArgsConstructor
public class MemberController {

    private final MemberRepository memberRepository;

    @PostConstruct
    public void init() {
        memberRepository.save(new Member("userA"));
        for (int i = 0; i < 100; i++) {
            memberRepository.save(new Member("user" + i));
        }
    }

    @GetMapping("/members/{id}")
    public String findMember(@PathVariable Long id) {
        Optional<Member> optionalMember = memberRepository.findById(id);
        return optionalMember.get().getUsername();
    }

    /*
        위의 API와 동일한 컨버팅 하는 기능을 Spring data JPA에서 제공한다.
        > 이런 도메인 클래스 컨버터는 매우 간단한 케이스에서만 사용이 가능하다.
        - 리포지토리를 사용해서 엔티티를 찾는다.
        도메인 클래스 컨버터로 엔티티를 파라메터로 받으면 단순 조회용으로만 사용해야한다.
        -> 트랜잭션 범위가 없는 케이스에서 조회 했기 때문에 엔티티를 수정하여도 DB에 반영되지 않음
     */
    @GetMapping("/members2/{id}")
    public String findMember2(@PathVariable("id") Member member) {
        return member.getUsername();
    }

    // Page 타입은 그대로 사용해도 좋음
    @GetMapping("/members")
    public Page<MemberDto> list(@PageableDefault(size = 5) Pageable pageable,
                             @Qualifier("member") Pageable memberPageable,
                             @Qualifier("order") Pageable orderPageable) {
        Pageable request = PageRequest.of(1, 2);
        // Pageable 파라메터를 받을수 있도록 지원 (페이지에 관련된 정보)
        // 인터페이스로 받지만, 스프링부트가 구현체로 받게끔 해준다.
        Page<Member> page = memberRepository.findAll(pageable);
        Page<MemberDto> map = page.map(member -> new MemberDto(member.getId(), member.getUsername(), null));
        return map;
    }
}
