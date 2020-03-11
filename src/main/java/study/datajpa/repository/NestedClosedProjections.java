package study.datajpa.repository;

/**
 * Created by IntelliJ IDEA.
 * User: june
 * Date: 2020-03-11
 * Time: 22:27
 **/
// Username, Team의 이름을 가져옴
public interface NestedClosedProjections {

    String getUsername();
    TeamInfo getTeam();

    interface TeamInfo {
        String getName();
    }
}
