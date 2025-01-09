package com.group.totalproject.security.jwt.constants;

/*
* - 로그인 필터 경로
* - 토큰 헤더
* - 토큰 헤더의 접두사(prefix)
* - 토큰 타입
*/
public class JwtConstants { // JWT와 관련된 상수 값 정의

    public static final String AUTH_LOGIN_URL = "/login"; // /login 요청을 보내면 인증 필터(JwtAuthenticationFilter)가 요청을 처리
    public static final String TOKEN_HEADER = "Authorization";  // JWT 토큰을 전달할 때 사용하는 HTTP 요청 헤더 이름
    public static final String TOKEN_PREFIX = "Bearer "; // 토큰 앞에 붙이는 접두사 ex) Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
    public static final String TOKEN_TYPE = "JWT"; // 토큰 유형: JWT

}
