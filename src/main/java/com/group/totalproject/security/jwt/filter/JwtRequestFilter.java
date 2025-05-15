package com.group.totalproject.security.jwt.filter;

import com.group.totalproject.security.jwt.constants.JwtConstants;
import com.group.totalproject.security.jwt.provider.JwtTokenProvider;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Set;

/**
 * JwtRequestFilter: 클라이언트의 요청에 포함된 토큰이 유효한지 확인하고, 유효하다면 사용자의 인증 정보를 SecurityContext에 저장하여 인증 상태를 유지
 * OncePerRequestFilter: 요청 당 한 번만 실행되는 필터를 구현하기 위한 추상 클래스
 * */
@Slf4j
@RequiredArgsConstructor
public class JwtRequestFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider; // JWT 토큰 생성, 파싱(해석), 유효성 검사 등을 담당하는 유틸 클래스.

   /**
    * JWT 요청 필터
    *  - request > headers > Authorization에 jwt 체크
    *  - JWT 토큰 유효성 검사
    * */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String ip = getClientIp(request);
        String method = request.getMethod();
        String uri = request.getRequestURI();
        log.info("[접속 IP] {} → {} {}", ip, method, uri);
        // 차단할 IP 목록 (필요하면 Set으로 필드로 뺄 수도 있음)
        Set<String> blockedIps = Set.of("2a06:98c0:3600::103");

        if (blockedIps.contains(ip)) {
            log.warn("[차단된 IP 접근 차단] {}", ip);
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Access denied");
            return; // 더 이상 필터 체인 진행하지 않음
        }

        // HttpServletRequest의 헤더에서 Authorization 값 읽어옴
        String header = request.getHeader(JwtConstants.TOKEN_HEADER); // String header = Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...

        // JWT 토큰이 없으면 다음 필터(filterChain.doFilter())로 이동
        // Bearer + {jwt} 체크
        if(header == null || header.isEmpty() || !header.startsWith(JwtConstants.TOKEN_PREFIX)) {
            filterChain.doFilter(request, response);
            return;
        }

        // JWT 토큰 있으면 Bearer 접두사를 제거하여 JWT 토큰만 추출: Bearer + {jwt} -> {jwt}
        String jwt = header.replace(JwtConstants.TOKEN_PREFIX, "");

        // jwtTokenProvider 토큰 해석하여 사용자 정보를 추출하고 이를 기반으로 Authentication 객체 생성
        Authentication authentication = jwtTokenProvider.getAuthentication(jwt);

        // JWT 유효성 검사
        if(jwtTokenProvider.validateToken(jwt)) {
            // JWT가 유효한 경우 SecurityContextHolder에 인증 객체를 저장
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        // 인증 및 검증이 끝나면 필터 체인을 통해 요청을 다음 단계로 전달
        filterChain.doFilter(request, response);

    }

    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        if (ip.contains(",")) {
            ip = ip.split(",")[0]; // 다수의 프록시 거친 경우 첫 IP가 클라이언트 IP
        }
        return ip;
    }

}
