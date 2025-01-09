package com.group.totalproject.dto.admin.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class AdminUpdateRequest {

    // private long id;
    private String email; // 이메일,

    @NotBlank(message = "이름을 입력해주세요")
    private String name; // 이름

    private Boolean emailVerified; // 이메일 검증 여부

}
