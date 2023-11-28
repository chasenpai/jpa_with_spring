package com.ex.entity;

import com.ex.repository.MemberRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Transactional
@Rollback(value = false)
class MemberTest {

    @PersistenceContext
    EntityManager em;

    @Autowired
    MemberRepository memberRepository;

    @Test
    void member() {

        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");
        em.persist(teamA);
        em.persist(teamB);

        Member memberA = new Member("memberA", 10, teamA);
        Member memberB = new Member("memberB", 20, teamA);
        Member memberC = new Member("memberC", 30, teamB);
        Member memberD = new Member("memberD", 40, teamB);
        em.persist(memberA);
        em.persist(memberB);
        em.persist(memberC);
        em.persist(memberD);

        em.flush();
        em.clear();

        List<Member> members = em.createQuery("select m from Member m", Member.class).getResultList();
        for (Member member : members) {
            System.out.println("member = " + member);
            System.out.println("ㄴmember.team = " + member.getTeam());
        }
    }

    @Test
    void baseEntityEvent() throws Exception {

        Member memberA = new Member("memberA", 10);
        memberRepository.save(memberA); //@PrePersist

        Thread.sleep(100);
        memberA.updateUsername("memberB");

        em.flush(); //@PreUpdate
        em.clear();

        Member findMember = memberRepository.findById(memberA.getId()).get();
        System.out.println("createdDate = " + findMember.getCreatedDate());
        System.out.println("lastModifiedDate = " + findMember.getLastModifiedDate());
        System.out.println("createdBy = " + findMember.getCreatedBy());
        System.out.println("lastModifiedBy = " + findMember.getModifiedBy());
    }

}
