package com.ex.repository;

import com.ex.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

//@Repository 생략 가능 - 스프링 데이터 JPA 가 자동으로 처리, 예외 변환 과정도 자동으로 처리
public interface MemberRepository extends JpaRepository<Member, Long> {
}
