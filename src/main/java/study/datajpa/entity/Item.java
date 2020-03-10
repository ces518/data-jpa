package study.datajpa.entity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.domain.Persistable;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.time.LocalDateTime;

/**
 * Created by IntelliJ IDEA.
 * User: june
 * Date: 2020-03-10
 * Time: 13:03
 **/
@EntityListeners(AuditingEntityListener.class)
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Item implements Persistable<String> {

    @Id @GeneratedValue
    private String id;

    @CreatedDate
    private LocalDateTime createdAt;

    public Item(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public boolean isNew() {
        // 생성일이 null 일 경우 새로운 엔티티로 판단하도록 엔티티 식별 로직을 구현
        return createdAt == null;
    }
}
