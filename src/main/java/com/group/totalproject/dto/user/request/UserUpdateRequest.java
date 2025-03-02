package com.group.totalproject.dto.user.request;

import lombok.Getter;

@Getter
public class UserUpdateRequest {
    private long id;
    private String name;
    private int pageSize;
}
