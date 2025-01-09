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
        log.info("authentication : " + authentication);

        // JWT 유효성 검사
        if(jwtTokenProvider.validateToken(jwt)) {
            log.info("유효한 JWT 토큰입니다.");

            // JWT가 유효한 경우 SecurityContextHolder에 인증 객체를 저장
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        // 인증 및 검증이 끝나면 필터 체인을 통해 요청을 다음 단계로 전달
        filterChain.doFilter(request, response);

    }
}
