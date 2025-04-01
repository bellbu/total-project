package com.group.totalproject.security.jwt.provider;


import com.group.totalproject.domain.admin.Admin;
import com.group.totalproject.domain.admin.AdminRepository;
import com.group.totalproject.domain.admin.Authority;
import com.group.totalproject.prop.JwtProps;
import com.group.totalproject.security.custom.CustomAdmin;
import com.group.totalproject.security.jwt.constants.JwtConstants;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
* JwtTokenProvider: JWT 토큰 관련 기능을 제공해주는 클래스
*  - 토큰 생성
*  - 토큰 파싱(해석)
*  - 토큰 유효성 검사
* */
@Slf4j
@RequiredArgsConstructor
@Component
public class JwtTokenProvider {

    private final JwtProps jwtProps; // JWT 설정 정보(비밀 키)를 담은 객체
    private final AdminRepository adminRepository;

    // 토큰 생성
    public String createToken(int adminNo, String email, List<String> authorities) {

        String jwt = Jwts.builder() // JWT 생성에 필요한 정보를 빌드
                .signWith(getShaKey(), Jwts.SIG.HS512) // 시그니처에 사용할 비밀키, 알고리즘 설정
                .header() // JWT 헤더 설정
                .add("type", JwtConstants.TOKEN_TYPE)  // type: JWT
                .and()
                .expiration(new Date(System.currentTimeMillis() + 180000))  // 토큰 만료 시간: 3분
                .claim("adminNo", "" + adminNo) // 사용자 번호를 클레임(페이로드의 한 조각)에 추가
                .claim("email", email) // 사용자 이름을 클레임(페이로드의 한 조각)에 추가
                .claim("authorities", authorities) // 권한 정보를 클레임(페이로드의 한 조각)에 추가
                .compact(); // 최종적으로 토큰 생성

        log.info("jwt : " + jwt);

        return jwt;
    }

    /**
     * 토큰 해석하여 인증 객체 생성
     * UsernamePasswordAuthenticationToken: 인증된 사용자 정보와 권한을 담은 인증 객체
     */
    public UsernamePasswordAuthenticationToken getAuthentication(String jwt) { // authHeader: Authorization 헤더에서 전달된 토큰 문자열(Bearer + {토큰})

        if(jwt == null || jwt.isEmpty())
            return null;

        try {
            // JWT 파싱(해석): Jwts.parser를 사용해 토큰을 해석하고 페이로드를 읽어옴
            Jws<Claims> parsedToken = Jwts.parser() // 파싱 객체를 생성: JWT를 파싱하거나 서명(signature)을 검증
                                        .verifyWith(getShaKey()) // 서명(signature)을 검증하기 위해 비밀 키를 설정
                                        .build() // JWT 파서를 완성
                                        .parseSignedClaims(jwt); // JWT를 파싱하고 검증을 수행

            // 인증된 사용자 이메일
            String email = parsedToken.getBody().get("email", String.class);

            // 토큰에 필요한 정보가 없는 경우 처리
            if (email == null || email.isEmpty()) {
                return null;
            }

            // Admin 객체 조회 및 유효성 검증
            Admin admin = adminRepository.findByEmail(email)
                    .orElseThrow(() -> new UsernameNotFoundException(email + " -> DB에서 관리자를 찾을 수 없습니다."));

            // 권한 리스트 생성
            List<GrantedAuthority> authorities = admin.getAuthorities().stream()
                                                    .map(Authority::name) // 열거형(enum) 데이터를 문자열(String)로 변환
                                                    .map(SimpleGrantedAuthority::new) // GrantedAuthority로 변환
                                                    .collect(Collectors.toList());

            return new UsernamePasswordAuthenticationToken(new CustomAdmin(admin), null, authorities); // 인증된 사용자(userDetails)와 해당 사용자의 권한들(authorities)을 스프링 시큐리티의 인증 객체로 생성하여 리턴

        } catch (ExpiredJwtException | UnsupportedJwtException | MalformedJwtException | IllegalArgumentException exception) {
            log.warn("JWT 처리 실패: {} - {}", jwt, exception.getMessage());
        }

        return null;
    }

    /**
     *  토큰 유효성 검사
     */
    public boolean validateToken(String jwt) {

        try {
            // JWT 파싱(해석)
            Jws<Claims> parsedToken = Jwts.parser()
                                        .verifyWith(getShaKey())
                                        .build()
                                        .parseSignedClaims(jwt);

            Date exp = parsedToken.getPayload().getExpiration(); // 만료 시간

            //
            /**
             * exp.before(new Date())
             *  - true: 만료 시간(exp)이 현재 시간(new Date())보다 이전임 → 토큰이 만료됨.
             *  - false: 만료 시간(exp)이 현재 시간(new Date()) 이후임 → 토큰이 유효함.
             * */
            return !exp.before(new Date()); // exp가 new Date()보다 이후여야 true 리턴

        } catch (ExpiredJwtException exception) {
            log.error("Token Expired");                 // 토큰 만료
            return false;
        } catch (JwtException exception) {
            log.error("Token Tampered");                // 토큰 손상
            return false;
        } catch (NullPointerException exception) {
            log.error("Token is null");                 // 토큰 없음
            return false;
        } catch (Exception e) {
            return false;
        }

    }

    // 비밀 키를 바이트 배열로 변환
    private byte[] getSigningKey() {
        return jwtProps.getSecretKey().getBytes();
    }

    // 바이트 배열로 변환한 비밀키를 HMAC-SHA 알고리즘에 적합한 SecretKey로 반환
    private SecretKey getShaKey() {
        return Keys.hmacShaKeyFor(getSigningKey());
    }

}