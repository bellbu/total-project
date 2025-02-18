package com.group.totalproject.security.custom;

import com.group.totalproject.domain.admin.Admin;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import java.util.Collection;
import java.util.stream.Collectors;

@RequiredArgsConstructor // final 필드를 매개변수로 하는 생성자 자동 생성
@Data
public class CustomAdmin implements UserDetails { // 인증된 사용자 정보를 스프링 시큐리티에서 활용하기 위해 UserDetails 인터페이스를 구현

    private final Admin admin; // CustomAdmin 클래스는 Admin을 감싸고 있는 래퍼 클래스

    /**
     * 권한 getter 메소드
     * Admin 엔티티의 권한 리스트(authorities)를 SimpleGrantedAuthority 형태로 변환 ex) ROLE_ADMIN, ROLE_USER
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return admin.getAuthorities().stream()
                .map(auth -> new SimpleGrantedAuthority(auth.name()))
                .collect(Collectors.toList());
    }

    @Override
    public String getUsername() {
        return admin.getEmail();
    }

    @Override
    public String getPassword() {
        return admin.getPassword();
    }

    /**
     * ↑사용자 정보 메서드
     * ↓계정 상태 확인 메서드
     */

    //
    @Override
    public boolean isAccountNonExpired() {
        return true; // 계정이 만료되지 않았다고 가정
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; // 계정이 잠기지 않았다고 가정
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // 인증 정보가 만료되지 않았다고 가정
    }

    @Override
    public boolean isEnabled() {
        return admin.getEmailVerified(); // 이메일 인증 여부에 따라 활성화 상태 반환
    }
}
