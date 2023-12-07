package com.ex.repository;

import com.ex.dto.MemberDto;
import com.ex.entity.Member;
import com.ex.projections.UsernameOnly;
import jakarta.persistence.LockModeType;
import jakarta.persistence.QueryHint;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

//@Repository 생략 가능 - 스프링 데이터 JPA 가 자동으로 처리, 예외 변환 과정도 자동으로 처리
public interface MemberRepository extends JpaRepository<Member, Long>, MemberRepositoryCustom {

    //메서드 이름으로 쿼리 생성
    //스프링 데이터 JPA 가 메소드 이름을 분석해서 JPQL 을 생성하고 실행
    //엔티티의 필드명이 일치하지 않으면 애플리케이션 로딩 시점에 오류가 발생
    //쿼리 메소드의 필터 조건을 따라야 함
    //SELECT - find...By, read...By, query...By, get...By, ...에 식별하기 위한 내용이 들어가도 된다
    //COUNT - count...By
    //EXISTS - exists...By
    //DELETE - delete...By, remove...By
    //DISTINCT - findDistinct ...
    //LIMIT - findFirst3..., findTop ...
    //https://docs.spring.io/spring-data/jpa/reference/jpa/query-methods.html
    List<Member> findByUsernameAndAgeGreaterThan(String username, int age);

    List<Member> findTop3By();

    long countByAgeLessThan(int age);

    List<Member> findByUsernameIn(List<String> names); //컬렉션 타입으로 IN 절 지원

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

    //다양한 반환 타입 지원
    List<Member> findListByUsername(String username);
    Member findMemberByUsername(String username); //두 개 이상일 시 NonUniqueResultException
    Optional<Member> findOptionalByUsername(String username);

    //페이징
    //카운트 쿼리 분리 - 카운트는 left join 등 할 필요없음
    //@Query(value = "select m from Member m left join m.team t", countQuery = "select count(m.username) from Member m")
    Page<Member> findByAge(int age, Pageable pageable);
    Slice<Member> findSliceByAge(int age, Pageable pageable);

    //벌크 업데이트
    @Modifying(clearAutomatically = true) //해당 애노테이션이 있어야 executeUpdate 를 실행, clearAutomatically > 영속성 컨텍스트 자동 초기화
    @Query("update Member m set m.age = m.age + 1 where m.age >= 20")
    int bulkAgePlus(@Param("age") int age);

    //페치 조인
    @Query("select m from Member m left join fetch m.team t")
    List<Member> findAllFetchJoin();

    //EntityGraph
    //페치 조인의 간편 버전
    @Override
    @EntityGraph(attributePaths = {"team"})
    List<Member> findAll();

    @EntityGraph(attributePaths = {"team"})
    List<Member> findEntityGraphByUsername(String username);

    @EntityGraph("Member.all") //NamedEntityGraph
    List<Member> findNamedEntityGraphByUsername(String username);

    //JPA 힌트 - JPA 구현체에게 제공하는 힌트
    @QueryHints(value = @QueryHint(name = "org.hibernate.readOnly", value = "true"))
    Member findReadOnlyByUsername(String username);

    //Lock
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Member findLockByUsername(String username);

    //인터페이스 기반 Projection
    List<UsernameOnly> findProjectionsByUsername(String username);

    //클래스 기반 Projection
    //List<UsernameOnlyDto> findProjectionsByUsername(String username);

    //동적 Projection
    <T> List<T> findProjectionsGenericByUsername(String username, Class<T> type);
}
