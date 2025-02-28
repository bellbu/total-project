package com.group.totalproject.dto.user.response;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.group.totalproject.domain.user.User;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Getter
public class UserResponse {
    private final long id;
    private final String name;
    private final Integer age;

    @JsonCreator // ✅ Jackson이 역직렬화할 때 사용할 생성자 지정
    public UserResponse(
            @JsonProperty("id") long id,
            @JsonProperty("name") String name,
            @JsonProperty("age") Integer age) {
        this.id = id;
        this.name = name;
        this.age = age;
    }

    public UserResponse(User user) { // ✅ id를 따로 받는 생성자는 불필요 → 제거 가능
        this.id = user.getId();
        this.name = user.getName();
        this.age = user.getAge();
    }

}
