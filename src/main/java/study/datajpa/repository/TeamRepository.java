package study.datajpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import study.datajpa.entity.Team;

/**
 * Created by IntelliJ IDEA.
 * User: june
 * Date: 2020-02-26
 * Time: 00:01
 **/
public interface TeamRepository extends JpaRepository<Team, Long> {
}
