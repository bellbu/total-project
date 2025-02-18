package com.group.totalproject.dto.admin.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class AdminUpdateRequest {

    // private long id;
    private String email; // 이메일,

    @NotBlank(message = "이름을 입력해주세요")
    private String name; // 이름

    @NotBlank(message = "비밀번호를 입력해주세요")
    private String password; // 비밀번호

    @NotBlank(message = "권한을 선택해주세요")
    private String authorities; // 권한 추가

    private Boolean emailVerified; // 이메일 검증 여부
}
