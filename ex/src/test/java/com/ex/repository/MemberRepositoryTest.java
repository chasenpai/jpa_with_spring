package com.ex.repository;

import com.ex.dto.MemberDto;
import com.ex.entity.Member;
import com.ex.entity.Team;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
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

        //DB는 반영되었지만 영속성 컨텍스트는 그대로 - 영속성 컨텍스트 초기화 필요
        List<Member> resultBeforeClear = memberRepository.findByUsername("memberE");
        assertThat(resultBeforeClear.get(0).getAge()).isEqualTo(30);

        em.flush();
        em.clear();

        List<Member> resultAfterClear = memberRepository.findByUsername("memberE");
        assertThat(resultAfterClear.get(0).getAge()).isEqualTo(31);
    }

}