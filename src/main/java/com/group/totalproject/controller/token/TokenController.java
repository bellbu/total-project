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
    public ResponseEntity<?> refreshToken(@CookieValue(value = "refreshToken", required = false) String refreshToken, HttpServletResponse response) {

        if (refreshToken == null || !jwtTokenProvider.validateToken(refreshToken)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("유효하지 않은 Refresh Token입니다.");
        }

        UsernamePasswordAuthenticationToken authentication = jwtTokenProvider.getAuthentication(refreshToken);
        if (authentication == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("인증 실패");
        }

        CustomAdmin customAdmin = (CustomAdmin) authentication.getPrincipal();
        Admin admin = customAdmin.getAdmin();

        String newAccessToken = jwtTokenProvider.createToken(
                Math.toIntExact(admin.getId()),
                admin.getEmail(),
                admin.getAuthorities().stream().map(Enum::name).collect(Collectors.toList())
        );

        // refreshToken도 재발급 (기존 방식처럼 쿠키에 설정)
        String newRefreshToken = jwtTokenProvider.createRefreshToken(Math.toIntExact(admin.getId()), admin.getEmail()); // 새로 생성
        response.addHeader("Set-Cookie", "refreshToken=" + newRefreshToken +
                "; HttpOnly; Path=/; Max-Age=600; SameSite=None; Secure");

        return ResponseEntity.ok().header(HttpHeaders.AUTHORIZATION, JwtConstants.TOKEN_PREFIX + newAccessToken)
                .body(newAccessToken);
    }
}
