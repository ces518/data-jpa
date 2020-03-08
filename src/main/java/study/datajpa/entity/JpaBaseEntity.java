package study.datajpa.entity;

import lombok.Getter;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import java.time.LocalDateTime;

/**
 * Created by IntelliJ IDEA.
 * User: june
 * Date: 2020-03-08
 * Time: 19:08
 **/
// 속성을 상속받는 JPA에서 제공하는 상속
@MappedSuperclass
@Getter
public class JpaBaseEntity {

    @Column(updatable = false)
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;

    /**
     * Persist 하기 이전에 콜백
     */
    @PrePersist
    public void prePersist() {
        LocalDateTime now = LocalDateTime.now();
        this.createdDate = now;
        this.updatedDate = now; // updateDate에 null이 있다면 쿼리가 매우 지저분해진다.
    }

    @PreUpdate
    public void perUpdate() {
        LocalDateTime now = LocalDateTime.now();
        this.updatedDate = now;
    }
}
