package com.group.totalproject.prop;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data // 모든 필드에 대한 getter, setter, toString, equals, hashCode 메서드 자동 생성
@Component
@ConfigurationProperties(prefix = "jwt") // application.yml 파일의 설정 값을 객체에 매핑
public class JwtProps { // JWT 관련 설정

    private String secretKey;  // jwt.secret-key 키와 매핑
}
