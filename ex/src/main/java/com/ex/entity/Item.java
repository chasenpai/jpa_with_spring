package com.ex.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.domain.Persistable;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Item implements Persistable<String> {

    /**
     * 새로운 엔티티를 구별하는 방법
     * - 새로운 엔티티면 persist
     * - 새로운 엔티티가 아니면 merge
     * - 식별자가 객체일 때 null 로 판단
     * - 식별자가 자바 기본 타입을 때 0으로 판단
     *
     * 만약 JPA 식별자 생성 전략이 @GenerateValue 일 경우 save 호출 시점에 식별자가
     * 없으므로 새로 엔티티로 인식해서 정상 동작한다. 하지만 JPA 식별자를 직접 할당해주면
     * 이미 식별자 값이 있는 상태로 save 를 호출하기 때문에 merge 가 호출된다
     * merge 는 우선 DB 를 호출해서 값을 확인하고 DB에 값이 없으면 새로운 엔티티로 인지하기
     * 때문에 매우 비효율 적이다
     * - Persistable 인터페이스를 구현해서 판단 로직 변경 가능
     */

    @Id
    private String id;

    @CreatedDate
    private LocalDateTime createdDate;

    public Item(String id) {
        this.id = id;
    }

    @Override
    public String getId() {
        return null;
    }

    @Override
    public boolean isNew() {
        return createdDate == null;
    }

}
