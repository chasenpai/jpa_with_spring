package com.ex.repository;

import com.ex.entity.Member;
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
class MemberJpaRepositoryTest {

    @Autowired
    MemberJpaRepository memberJpaRepository;

    @Test
    void member() {

        Member member = new Member("memberA");
        Member savedMember = memberJpaRepository.save(member);
        Member findMember = memberJpaRepository.find(savedMember.getId());

        assertThat(savedMember.getId()).isEqualTo(findMember.getId());
        assertThat(findMember.getUsername()).isEqualTo(member.getUsername());
        assertThat(findMember).isEqualTo(member);
    }

    @Test
    void crud() {

        Member memberA = new Member("memberA");
        Member memberB = new Member("memberB");
        memberJpaRepository.save(memberA);
        memberJpaRepository.save(memberB);

        Member findMemberA = memberJpaRepository.findById(memberA.getId()).get();
        Member findMemberB = memberJpaRepository.findById(memberB.getId()).get();
        assertThat(findMemberA).isEqualTo(memberA);
        assertThat(findMemberB).isEqualTo(memberB);

        List<Member> members = memberJpaRepository.findAll();
        assertThat(members.size()).isEqualTo(2);

        long count = memberJpaRepository.count();
        assertThat(count).isEqualTo(2);

        memberJpaRepository.delete(memberA);
        memberJpaRepository.delete(memberB);

        long deletedCount = memberJpaRepository.count();
        assertThat(deletedCount).isEqualTo(0);
    }

}