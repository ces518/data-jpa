package study.datajpa.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import study.datajpa.entity.Item;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ItemRepositoryTest {

    @Autowired ItemRepository itemRepository;

    @Test
    public void save() {
        // @GeneratedValue 를 사용할 경우
        Item item = new Item(LocalDateTime.now());
        itemRepository.save(item); // JPA에 Persist 하는 순간에 내부에서 ID가 생성됨

        /////////////////////
        // @GeneratedValue 를 사용하지 않고, 채번 테이블 등을 사용해서 PK값을 지정하는 경우
        // PK값이 존재하기 때문에 merge가 호출된다.
        // -> DB에 있다고 가정한다. (select 쿼리가 먼저 나간다)
        // 비효율적임. (select 쿼리가 1번 더 나감)
    }
}