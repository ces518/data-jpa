package study.datajpa.repository;

import study.datajpa.entity.Member;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: june
 * Date: 2020-03-08
 * Time: 10:40
 **/
public interface MemberRepositoryCustom {
    List<Member> findMemberCustom();
}
