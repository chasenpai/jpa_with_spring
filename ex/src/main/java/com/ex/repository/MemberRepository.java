package com.ex.repository;

import com.ex.dto.MemberDto;
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

    //리포지토리 메서드에 쿼리 정의
    //실행할 메서드에 정적 쿼리를 직접 작성하므로 이름 없는 Named 쿼리라 할 수 있음
    //애플리케이션 실행 시점에 문법 오류를 알 수 있는게 매우 큰 장점
    @Query("select m from Member m where m.username = :username and m.age =:age")
    List<Member> findMember(@Param("username") String username, @Param("age") int age);

    //단순히 값을 조회(JPA 값 타입도 가능)
    @Query("select m.username from Member m")
    List<String> findUsernameList();

    //DTO 로 직접 조회
    //new 명령어를 사용해야 한다(JPA 와 동일)
    @Query("select new com.ex.dto.MemberDto(m.id, m.username, t.name) from Member m join m.team t")
    List<MemberDto> findMemberDto();

}
