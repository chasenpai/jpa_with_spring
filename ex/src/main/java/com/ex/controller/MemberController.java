package com.ex.controller;

import com.ex.dto.MemberDto;
import com.ex.entity.Member;
import com.ex.repository.MemberRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class MemberController {

    private final MemberRepository memberRepository;

    /**
     * 도메인 클래스 컨버터
     * - HTTP 파라미터로 넘어온 엔티티의 아이디로 엔티티 객체를 찾아서 바인딩
     * - 도메인 클래스 컨버터를 사용할 경우 엔티티는 단순 조회 용도로만 사용해야 한다
     * - 트랜잭션이 없는 범위에서 엔티티를 조회했기 때문이다
     * - 그냥 예제 같은거 아니면 사용하지말자
     */
    @GetMapping("/members/{id}")
    public String findMemberOne(@PathVariable("id") Member member) {
        return member.getUsername();
    }

    /**
     * 페이징&정렬 확장
     * - PageRequest 객체를 생성해준다
     * - /members?page=0&size=3&sort=id,desc&sort=username,desc
     * - @PageableDefault 로 사이즈와 정렬 등을 개별 설정할 수 있음
     * - 페이징 정보가 둘 이상이면 @Qualifier("member") 접두사로 구분 가능
     * - 응답에 현재 페이지, 전체 페이지 수, 페이지 사이즈 등 페이징과 정렬에 대한 정보 제공
     */
    @GetMapping("/members")
    public Page<MemberDto> findMembers(@PageableDefault(size = 5, sort = "username") Pageable pageable) {
        return memberRepository.findAll(pageable).map(MemberDto::new);
    }

    @PostConstruct
    public void init() {
        for(int i = 1; i <= 100; i++) {
            memberRepository.save(new Member("member" + i, 20 + i));
        }
    }

}
