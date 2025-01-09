package com.group.totalproject.dto.admin.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class AdminLoginRequest {

    @NotBlank(message= "이메일을 입력해주세요")
    @Email(message = "유효한 이메일 형식이어야 합니다.")
    private String email;

    @NotBlank(message = "비밀번호를 입력해주세요.")
    private String password;

}
