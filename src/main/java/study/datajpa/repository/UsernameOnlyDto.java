package study.datajpa.repository;

/**
 * Created by IntelliJ IDEA.
 * User: june
 * Date: 2020-03-11
 * Time: 22:23
 **/
public class UsernameOnlyDto {

    private final String username;

    // 생성자의 파라메터명과 매칭시켜 Projection이 동작하낟.
    public UsernameOnlyDto(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }
}
