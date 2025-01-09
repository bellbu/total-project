package com.group.totalproject.security.custom;

import com.group.totalproject.domain.admin.Admin;
import com.group.totalproject.domain.admin.AdminRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Service
public class CustomAdminDetailService implements UserDetailsService {

    private final AdminRepository adminRepository;

    /**
     * loadUserByUsername(): 스프링 시큐리티가 사용자 인증 시 호출하는 메서드
     * AuthenticationManager가 호출
     * */
    @Override
    @Transactional
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        log.info("email : " + email);
        Admin admin = adminRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(email + " -> DB에서 관리자를 찾을 수 없습니다."));
        return new CustomAdmin(admin);
    }

}
