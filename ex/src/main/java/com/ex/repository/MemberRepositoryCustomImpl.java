package com.ex.repository;

import com.ex.entity.Member;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
//구현 클래스 네이밍 규칙
//MemberRepository + Impl 또는 MemberRepositoryCustom + Impl > 스프링 데이터 JPA 가 인식해서 스프링 빈으로 등록
public class MemberRepositoryCustomImpl implements MemberRepositoryCustom {

    private final EntityManager em;

    @Override
    public List<Member> findMemberCustom() {
        return em.createQuery("select m from Member m", Member.class)
                .getResultList();
    }

}
