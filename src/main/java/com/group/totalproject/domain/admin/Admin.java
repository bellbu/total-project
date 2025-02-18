package com.group.totalproject.domain.admin;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Admin {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // pk 자동 생성 전략
    private Long id = null;

    @Column(nullable = false, unique = true)
    private String email; // 이메일,

    @Column(nullable = false)
    private String name; // 이름

    @Column(nullable = false)
    private String password; // 비밀번호

    @ElementCollection(fetch = FetchType.EAGER) // 다중 권한을 지원하기 위해 사용
    @Enumerated(EnumType.STRING) // @Enumerated: 필드 Enum 타입으로 지정, EnumType.STRING: DB에 값을 String으로 저장
    private List<Authority> authorities = new ArrayList<>(); // 권한(관리자, 회원)

    @Column(nullable = false)
    private Boolean emailVerified = false; // 이메일 검증 여부

    @Column(updatable = false) // 업데이트되지 않도록 설정
    private LocalDateTime regDate; // 등록일자

    @Column
    private LocalDateTime visitDate; // 방문일자

    @PrePersist // 엔티티 생성되기 전 실행
    public void prePersist() {
        this.regDate = LocalDateTime.now();
        this.visitDate = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        this.visitDate = LocalDateTime.now();
    }

    @Builder // 빌더 패턴 사용: 생성 시 입력에 필요한 필드만 설정 
    public Admin(String email, String name, String password, List<Authority> authorities, Boolean emailVerified) {
        this.email = email;
        this.name = name;
        this.password = password;
        this.authorities = authorities;
        this.emailVerified = emailVerified != null ? emailVerified : false;
    }

    public void updateAdmin(String name, String password, List<Authority> authorities, Boolean emailVerified) {
        this.name = name;
        this.password = password;
        this.authorities = authorities;
        this.emailVerified = emailVerified;
    }

    @Override
    public String toString() {
        return "Admin{" +
                "id=" + id +
                ", email='" + email + '\'' +
                ", name='" + name + '\'' +
                ", password='" + password + '\'' +
                ", authorities=" + authorities +
                ", emailVerified=" + emailVerified +
                ", regDate=" + regDate +
                ", visitDate=" + visitDate +
                '}';
    }
}
