package com.ex.repository;

import com.ex.dto.MemberDto;
import com.ex.entity.Member;
import com.ex.entity.Team;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.*;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Transactional
@Rollback(value = false)
class MemberRepositoryTest {

    @Autowired
    MemberRepository memberRepository; //스프링 데이터 JPA 가 프록시로 구현체를 만들어준다
    @Autowired
    TeamRepository teamRepository;
    @PersistenceContext
    EntityManager em;

    @Test
    void member() {

        Member member = new Member("memberA");
        Member savedMember = memberRepository.save(member);
        Member findMember = memberRepository.findById(savedMember.getId()).get();

        assertThat(savedMember.getId()).isEqualTo(findMember.getId());
        assertThat(findMember.getUsername()).isEqualTo(member.getUsername());
        assertThat(findMember).isEqualTo(member);
    }

    @Test
    void crud() {

        Member memberA = new Member("memberA");
        Member memberB = new Member("memberB");
        memberRepository.save(memberA);
        memberRepository.save(memberB);

        Member findMemberA = memberRepository.findById(memberA.getId()).get();
        Member findMemberB = memberRepository.findById(memberB.getId()).get();
        assertThat(findMemberA).isEqualTo(memberA);
        assertThat(findMemberB).isEqualTo(memberB);

        List<Member> members = memberRepository.findAll();
        assertThat(members.size()).isEqualTo(2);

        long count = memberRepository.count();
        assertThat(count).isEqualTo(2);

        memberRepository.delete(memberA);
        memberRepository.delete(memberB);

        long deletedCount = memberRepository.count();
        assertThat(deletedCount).isEqualTo(0);
    }

    @Test
    void findByUsernameAndAgeGreaterThan() {

        Member memberA = new Member("KIM", 10);
        Member memberB = new Member("KIM", 20);
        memberRepository.save(memberA);
        memberRepository.save(memberB);

        List<Member> result = memberRepository.findByUsernameAndAgeGreaterThan("KIM", 15);

        assertThat(result.size()).isEqualTo(1);
        assertThat(result.get(0).getUsername()).isEqualTo("KIM");
        assertThat(result.get(0).getAge()).isEqualTo(20);
    }
    
    @Test
    void findTop3() {

        Member memberA = new Member("memberA", 10);
        Member memberB = new Member("memberB", 20);
        Member memberC = new Member("memberC", 30);
        Member memberD = new Member("memberD", 40);
        memberRepository.save(memberA);
        memberRepository.save(memberB);
        memberRepository.save(memberC);
        memberRepository.save(memberD);
        
        List<Member> result = memberRepository.findTop3By();
        assertThat(result.size()).isEqualTo(3);
    }

    @Test
    void countByAgeLessThan() {

        Member memberA = new Member("memberA", 10);
        Member memberB = new Member("memberB", 20);
        Member memberC = new Member("memberC", 30);
        Member memberD = new Member("memberD", 40);
        memberRepository.save(memberA);
        memberRepository.save(memberB);
        memberRepository.save(memberC);
        memberRepository.save(memberD);

        long result = memberRepository.countByAgeLessThan(30);
        assertThat(result).isEqualTo(2);
    }
    
    @Test
    void findByUsernameIn() {

        Member memberA = new Member("memberA", 10);
        Member memberB = new Member("memberB", 20);
        Member memberC = new Member("memberC", 30);
        memberRepository.save(memberA);
        memberRepository.save(memberB);
        memberRepository.save(memberC);
        
        List<Member> result = memberRepository.findByUsernameIn(List.of(memberA.getUsername(), memberB.getUsername()));
        assertThat(result.size()).isEqualTo(2);
    }

    @Test
    void namedQuery() {

        Member memberA = new Member("memberA");
        Member memberB = new Member("memberB");
        memberRepository.save(memberA);
        memberRepository.save(memberB);

        List<Member> result = memberRepository.findByUsername("memberA");
        assertThat(result.get(0).getUsername()).isEqualTo("memberA");
    }

    @Test
    void queryAnnotation() {

        Member memberA = new Member("memberA", 20);
        Member memberB = new Member("memberB", 30);
        memberRepository.save(memberA);
        memberRepository.save(memberB);

        List<Member> result = memberRepository.findMember("memberA", 20);
        assertThat(result.get(0)).isEqualTo(memberA);
    }

    @Test
    void findUsernameList() {

        Member memberA = new Member("memberA", 20);
        Member memberB = new Member("memberB", 30);
        memberRepository.save(memberA);
        memberRepository.save(memberB);

        List<String> result = memberRepository.findUsernameList();
        assertThat(result.get(0)).isEqualTo("memberA");
        assertThat(result.get(1)).isEqualTo("memberB");
    }

    @Test
    void findMemberDto() {

        Team teamA = new Team("teamA");
        teamRepository.save(teamA);

        Member memberA = new Member("memberA", 20, teamA);
        memberRepository.save(memberA);
        
        List<MemberDto> result = memberRepository.findMemberDto();
        assertThat(result.get(0).getUsername()).isEqualTo("memberA");
        assertThat(result.get(0).getTeamName()).isEqualTo("teamA");
    }

    @Test
    void returnType() {

        Member memberA = new Member("memberA");
        memberRepository.save(memberA);

        List<Member> list = memberRepository.findListByUsername("memberA");
        Member member = memberRepository.findMemberByUsername("memberA");
        Optional<Member> optional = memberRepository.findOptionalByUsername("memberA");
    }

    @Test
    void paging() {

        memberRepository.save(new Member("memberA", 10));
        memberRepository.save(new Member("memberB", 10));
        memberRepository.save(new Member("memberC", 10));
        memberRepository.save(new Member("memberD", 10));
        memberRepository.save(new Member("memberE", 10));

        int age = 10;

        //Page 는 1이 아닌 0부터 시작, 첫번째 파라미터는 현재 페이지, 두번째 파라미터는 조회할 데이터 수
        PageRequest pageRequest = PageRequest.of(0, 3,
                Sort.by(Sort.Direction.DESC, "username"));

        Page<Member> page = memberRepository.findByAge(age, pageRequest);

        List<Member> content = page.getContent();
        long totalElements = page.getTotalElements();

        assertThat(content.size()).isEqualTo(3); //조회된 데이터 수
        assertThat(totalElements).isEqualTo(5); //전체 데이터 수
        assertThat(page.getNumber()).isEqualTo(0); //페이지 번호
        assertThat(page.getTotalPages()).isEqualTo(2); //전체 페이지 수
        assertThat(page.isFirst()).isTrue(); //첫번째 항목 여부
        assertThat(page.hasNext()).isTrue(); //다음 페이지 유무

        //Slice - limit + 1 > 다음 페이지 여부 확인
//        Slice<Member> slice = memberRepository.findSliceByAge(age, pageRequest);

        //DTO 변환
        Page<MemberDto> pageDto = page.map(m -> new MemberDto(m.getId(), m.getUsername(), null));
    }

    @Test
    void bulkUpdate() {

        memberRepository.save(new Member("memberA", 10));
        memberRepository.save(new Member("memberB", 15));
        memberRepository.save(new Member("memberC", 20));
        memberRepository.save(new Member("memberD", 25));
        memberRepository.save(new Member("memberE", 30));

        int updatedCount = memberRepository.bulkAgePlus(20);
        assertThat(updatedCount).isEqualTo(3);

        //DB는 반영되었지만 영속성 컨텍스트는 그대로
        List<Member> resultBeforeClear = memberRepository.findByUsername("memberE");
        assertThat(resultBeforeClear.get(0).getAge()).isEqualTo(30);

        //영속성 컨텍스트에 엔티티가 없는 상태에서 벌크 연산을 먼저하는 것이 좋다
        //부득이하게 영속성 컨텍스트에 엔티티가 있으면 벌크 연산 직후 컨텍스트를 초기화 한다
        em.flush();
        em.clear();

        List<Member> resultAfterClear = memberRepository.findByUsername("memberE");
        assertThat(resultAfterClear.get(0).getAge()).isEqualTo(31);
    }

    @Test
    void findMemberLazy() {

        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");
        teamRepository.save(teamA);
        teamRepository.save(teamB);

        Member memberA = new Member("memberA", 10, teamA);
        Member memberB = new Member("memberB", 10, teamB);
        memberRepository.save(memberA);
        memberRepository.save(memberB);

        em.flush();
        em.clear();

        //Fetch Join
        List<Member> fetchResult = memberRepository.findAllFetchJoin();
        for (Member member : fetchResult) {
            System.out.println("member = " + member.getUsername());
            System.out.println("member.teamClass = " + member.getTeam().getClass());
            System.out.println("member.team = " + member.getTeam().getName());
        }

        em.flush();
        em.clear();

        //EntityGraph
        List<Member> graphResult1 = memberRepository.findAll();
        for (Member member : graphResult1) {
            System.out.println("member = " + member.getUsername());
            System.out.println("member.teamClass = " + member.getTeam().getClass());
            System.out.println("member.team = " + member.getTeam().getName());
        }

        em.flush();
        em.clear();

        List<Member> graphResult2 = memberRepository.findEntityGraphByUsername("memberA");
        System.out.println("member = " + graphResult2.get(0).getUsername());
        System.out.println("member.teamClass = " + graphResult2.get(0).getTeam().getClass());
        System.out.println("member.team = " + graphResult2.get(0).getTeam().getName());

        em.flush();
        em.clear();

        List<Member> graphResult3 = memberRepository.findNamedEntityGraphByUsername("memberA");
        System.out.println("member = " + graphResult3.get(0).getUsername());
        System.out.println("member.teamClass = " + graphResult3.get(0).getTeam().getClass());
        System.out.println("member.team = " + graphResult3.get(0).getTeam().getName());
    }

    @Test
    void queryHint() {

        Member memberA = new Member("memberA", 10);
        memberRepository.save(memberA);

        em.flush();
        em.clear();

        Member findMember = memberRepository.findReadOnlyByUsername(memberA.getUsername());
        findMember.updateUsername("memberB"); //read only - 변경 무시, 내부적으로 최적화를 다 해버리고 스냅샷을 만들지 않음

        em.flush();
    }

    @Test
    void lock() {

        Member memberA = new Member("memberA", 10);
        memberRepository.save(memberA);

        em.flush();
        em.clear();

        Member findMember = memberRepository.findLockByUsername(memberA.getUsername());
    }

    @Test
    void callCustom() {

        Member memberA = new Member("memberA", 10);
        memberRepository.save(memberA);

        List<Member> members = memberRepository.findMemberCustom();
        assertThat(members.size()).isEqualTo(1);
    }

    @Test
    void queryByExample() {

        Team teamA = new Team("teamA");
        em.persist(teamA);

        Member memberA = new Member("memberA", 0 , teamA);
        Member memberB = new Member("memberB", 0 , teamA);
        em.persist(memberA);
        em.persist(memberB);

        em.flush();
        em.clear();

        /**
         * QueryByExample
         * - 동적 쿼리를 편리하게 처리하고 도메인 객체를 그대로 사용
         * - 데이터 저장소를 RDB 에서 NOSQL 로 변경해도 코드 변경이 없게 추상화 되어 있다
         * - 스프링 데이터 JpaRepository 에 포함되어 있어 바로 사용 가능
         * - 하지만 내부 조인만 가능하고 외부 조인을 불가능
         * - 중첩 제약조건이 안되는 등 여러가지 제약이 따름
         * - 단순한 매칭 조건만 지원
         * - 그냥 QueryDSL 쓰자
         */

        //Probe - 필드에 데이터가 있는 실제 도메인 객체
        Member member = new Member("memberA");
        Team team = new Team("teamA");
        member.changeTeam(team);
        //ExampleMatcher - 특정 필드를 일치시키는 상세한 정보 제공, 재사용 가능
        ExampleMatcher matcher = ExampleMatcher.matching().withIgnorePaths("age");
        //Example - Probe 와 ExampleMatcher 로 구성, 쿼리를 생성하는데 사용
        Example<Member> example = Example.of(member, matcher);

        List<Member> result = memberRepository.findAll(example);
        assertThat(result.size()).isEqualTo(1);
        assertThat(result.get(0).getUsername()).isEqualTo("memberA");
    }

}