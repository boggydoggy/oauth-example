package com.altimedia.example.oauth.domain;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@MappedSuperclass //클래스가 만들어지지 않는 기초 클래스를 의미
@Getter
@EntityListeners(value = {AuditingEntityListener.class}) //Entity의 변화를 감지하는 리스너
public class Base_Entity {
    @CreatedDate // 만들어질 때 입력 되는 필드를 정의
    @Column(name = "create_dt", updatable = false)
    private LocalDateTime createDate;

    @LastModifiedDate //마지막 수정 때 입력되는 필드 정의
    @Column(name = "modify_dt")
    private LocalDateTime modifyDate;
}
