package study.datajpa.repository;

import org.springframework.beans.factory.annotation.Value;

/**
 * Created by IntelliJ IDEA.
 * User: june
 * Date: 2020-03-11
 * Time: 22:15
 **/
public interface UserNameOnly {

    // SpEL 을 사용
    // Member Entity의 Property를 모두 조회 한뒤 SpEL에 명시한 데이터를 조합
    // DB에서 모두 조회한뒤 처리 하는것 open projection
    @Value("#{target.username + ' ' + target.age}")
    String getUsername();

    // close projection은 필요한것만 정확하게 가져오는것
}
