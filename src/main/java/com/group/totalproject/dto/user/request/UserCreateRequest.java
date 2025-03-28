package com.group.totalproject.dto.user.request;

import lombok.Getter;

@Getter
public class UserCreateRequest {

    private String name;
    private Integer age; // Integer: null 표현 가능 / int: null 표현 불가능
    private Integer pageSize;

}
