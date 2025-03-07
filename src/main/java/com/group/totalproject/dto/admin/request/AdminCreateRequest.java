package com.group.totalproject.dto.admin.request;

import com.group.totalproject.domain.admin.Admin;
import com.group.totalproject.domain.admin.Authority;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.util.Collections;


@Getter
public class AdminCreateRequest implements AdminRequest {

    @NotBlank(message = "이메일을 입력해주세요.")
    @Email(message = "유효한 이메일 형식이어야 합니다.")
    private String email; // 이메일,

    @NotBlank(message = "이름을 입력해주세요")
    private String name; // 이름

    @NotBlank(message = "비밀번호를 입력해주세요")
    private String password; // 비밀번호

    private Boolean emailVerified; // 이메일 검증 여부
    
    @NotBlank(message = "권한을 선택해주세요")
    private String authorities; // 권한 추가

    public Admin toEntity(PasswordEncoder passwordEncoder){
        return Admin.builder() // 빌더 패턴 사용해 객체 생성
                .email(email) // DTO email 값을 엔티티의 email 필드에 할당
                .name(name)
                .password(passwordEncoder.encode(password))
                .authorities(Collections.singletonList(Authority.valueOf(authorities))) // role 값에 따라 권한 설정
                .emailVerified(emailVerified)
                .build(); // 객체 생성 완료
    }

}
