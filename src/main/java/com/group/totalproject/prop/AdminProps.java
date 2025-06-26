package com.group.totalproject.prop;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration // 해당 클래스 빈으로 등록
@ConfigurationProperties(prefix = "custom") // application.yml에서 "custom"으로 시작하는 프로퍼티 바인딩
@Getter
@Setter
public class AdminProps {
    // custom.admin.* 설정 값을 바인딩할 Account 타입 필드 선언
    private Account admin;
    // custom.subadmin.* 설정 값을 바인딩할 Account 타입 필드 선언
    private Account subadmin;


    @Getter
    @Setter
    public static class Account { // admin, subadmin 아래의 email, password 값을 받기 위한 내부 클래스
        // custom.admin.email, custom.subadmin.email 매핑
        private String email;
        // custom.admin.password, custom.subadmin.password 매핑
        private String password;
    }
}
