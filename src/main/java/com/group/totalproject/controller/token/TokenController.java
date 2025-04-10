package com.group.totalproject.controller.token;

import com.group.totalproject.domain.admin.Admin;
import com.group.totalproject.domain.admin.AdminRepository;
import com.group.totalproject.security.custom.CustomAdmin;
import com.group.totalproject.security.jwt.constants.JwtConstants;
import com.group.totalproject.security.jwt.provider.JwtTokenProvider;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@RestController
@RequiredArgsConstructor
@RequestMapping("/token")
public class TokenController {

    private final JwtTokenProvider jwtTokenProvider;

    @PostMapping("/refresh-token")
    public ResponseEntity<?> refreshToken(@CookieValue(value = "refreshToken", required = false) String refreshToken, // @CookieValue: 요청 헤더 중 Cookie에서 refreshToken이라는 이름의 쿠키 값을 꺼냄
                                          HttpServletResponse response) {

        // refreshToken 유효성 검증
        if (refreshToken == null || !jwtTokenProvider.validateToken(refreshToken)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("유효하지 않은 Refresh Token입니다.");
        }

        // refreshToken 해석하여 인증 객체 생성
        UsernamePasswordAuthenticationToken authentication = jwtTokenProvider.getAuthentication(refreshToken);

        if (authentication == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("인증 실패");
        }

        // 인증된 사용자 정보 가져오기
        CustomAdmin customAdmin = (CustomAdmin) authentication.getPrincipal();
        Admin admin = customAdmin.getAdmin();

        // 새 accessToken 발급
        String newAccessToken = jwtTokenProvider.createToken(
                Math.toIntExact(admin.getId()),
                admin.getEmail(),
                admin.getAuthorities().stream().map(Enum::name).collect(Collectors.toList())
        );

        // 새 refreshToken도 발급해서 쿠키에 설정
        String newRefreshToken = jwtTokenProvider.createRefreshToken(Math.toIntExact(admin.getId()), admin.getEmail());
        response.addHeader("Set-Cookie", "refreshToken=" + newRefreshToken +
                "; HttpOnly; Path=/; Max-Age=600; SameSite=None; Secure");

        // 새로 발급된 accessToken 응답 헤더와 본문에 전달
        return ResponseEntity.ok().header(HttpHeaders.AUTHORIZATION, JwtConstants.TOKEN_PREFIX + newAccessToken)
                .body(newAccessToken);
    }
}
