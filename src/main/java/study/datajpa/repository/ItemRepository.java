package study.datajpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import study.datajpa.entity.Item;

/**
 * Created by IntelliJ IDEA.
 * User: june
 * Date: 2020-03-10
 * Time: 13:04
 **/
public interface ItemRepository extends JpaRepository<Item, Long> {
}
