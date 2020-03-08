package study.datajpa;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import java.util.Optional;
import java.util.UUID;

@EnableJpaAuditing
@SpringBootApplication
// SpringBoot 이기 때문에 생략이 가능하다.
//@EnableJpaRepositories(basePackages = "study.datajpa.repository")
public class DataJpaApplication {

    public static void main(String[] args) {
        SpringApplication.run(DataJpaApplication.class, args);
    }

    @Bean
    public AuditorAware<String> auditorProvider() {
        // AuditorAware의 getCurrentAware를 구현한다.
        // 여기서 리턴해주는 값을 @CreatedBy, @LastModifiedBy 의 값에 세팅해준다.
        return () -> Optional.of(UUID.randomUUID().toString());
    }
}
