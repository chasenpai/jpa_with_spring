package com.ex.repository;

import com.ex.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;

//@Repository 생략 가능 - 스프링 데이터 JPA 가 자동으로 처리, 예외 변환 과정도 자동으로 처리
public interface MemberRepository extends JpaRepository<Member, Long> {

    //메서드 이름으로 쿼리 생성
    //스프링 데이터 JPA 가 메소드 이름을 분석해서 JPQL 을 생성하고 실행
    //엔티티의 필드명이 일치하지 않으면 애플리케이션 로딩 시점에 오류가 발생
    List<Member> findByUsernameAndAgeGreaterThan(String username, int age);

    List<Member> findTop3By();

    long countByAgeLessThan(int age);

    List<Member> findByUsernameIn(List<String> names);

    //Named 쿼리 호출
    //JPQL 을 명확하게 작성했을 땐 @Param 애노테이션이 필요
    //실행할 Named 쿼리가 없으면 메서드 이름으로 쿼리 생성 전략을 사용한다
    //실무에서 거의 사용되지 않음
    @Query(name = "Member.findByUsername")
    List<Member> findByUsername(@Param("username") String username);
}
