package com.ex;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.util.Optional;
import java.util.UUID;

@EnableJpaAuditing
//@EnableJpaAuditing(modifyOnCreate = false) //업데이트는 NULL 로 들어감 > 보통 관례상 등록, 수정일자 둘다 넣음
@SpringBootApplication
public class ExApplication {

	public static void main(String[] args) {
		SpringApplication.run(ExApplication.class, args);
	}

	@Bean
	public AuditorAware<String> auditorAwareProvider() {
		return () -> Optional.of(UUID.randomUUID().toString());
	}

}
