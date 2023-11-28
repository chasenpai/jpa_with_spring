package com.ex.entity;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import lombok.Getter;

import java.time.LocalDateTime;

@MappedSuperclass
@Getter
public class JpaBaseEntity {

    //순수 JPA 기반 BaseEntity

    @Column(updatable = false)
    private LocalDateTime createdDate;

    private LocalDateTime updatedDate; //데이터 수정 시간을 알 수 있으면 굉장히 편리하다..

    @PrePersist //저장되기 전 실행
    public void prePersist() {
        LocalDateTime now = LocalDateTime.now();
        this.createdDate = now;
        this.updatedDate = now;
    }

    @PreUpdate //업데이트 전 실행
    public void preUpdate() {
        this.updatedDate = LocalDateTime.now();
    }

}
