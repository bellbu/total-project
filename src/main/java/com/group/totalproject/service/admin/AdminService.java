package com.group.totalproject.service.admin;

import com.group.totalproject.controller.admin.AdminController;
import com.group.totalproject.domain.admin.Admin;
import com.group.totalproject.domain.admin.AdminRepository;
import com.group.totalproject.domain.admin.Authority;
import com.group.totalproject.dto.admin.request.AdminCreateRequest;
import com.group.totalproject.dto.admin.request.AdminRequest;
import com.group.totalproject.dto.admin.request.AdminUpdateRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

@Slf4j
@RequiredArgsConstructor // 의존성 자동 주입(필드 주입이 아닌 생성자 주입)
@Service
public class AdminService {

    private final PasswordEncoder passwordEncoder;
    private final AdminRepository adminRepository;

    // 관리자 등록
    @Transactional
    public int saveAdmin(AdminCreateRequest request) {
        log.info("[관리자 등록 시도] 이메일: {}", request.getEmail());

        // 유효성 검사
        validateRequest(request);

        if (adminRepository.existsByEmail(request.getEmail())) {
            log.warn("[관리자 등록 실패] 이미 존재하는 이메일: {}", request.getEmail());
            throw new IllegalArgumentException("이미 가입되어 있는 이메일입니다.");
        }

        if (!Boolean.TRUE.equals(request.getEmailVerified())) {
            log.warn("[관리자 등록 실패] 이메일 인증되지 않음: {}", request.getEmail());
            throw new IllegalArgumentException("이메일 인증이 필요합니다.");
        }

        // 관리자 엔티티 생성
        Admin admin = request.toEntity(passwordEncoder);

        // 회원 등록
        Admin savedAdmin = adminRepository.save(admin);

        boolean success = savedAdmin.getId() != null;
        log.info("[관리자 등록 결과] 이메일: {}, 성공 여부: {}", request.getEmail(), success);

        // 저장 성공 여부 반환
        return savedAdmin.getId() != null ? 1 : 0;
    }

    // 유효성 검사 메서드
    private void validateRequest(AdminRequest request) {

        if (request.getEmail() == null || request.getEmail().trim().isEmpty()) {
            log.warn("[유효성 검사 실패] 이메일 누락");
            throw new IllegalArgumentException("이메일을 입력해 주세요.");
        }

        if (!request.getEmail().matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")) {
            log.warn("[유효성 검사 실패] 이메일 형식 오류: {}", request.getEmail());
            throw new IllegalArgumentException("올바른 이메일 형식을 입력해 주세요.");
        }

        if (request.getName() == null || request.getName().trim().isEmpty()) {
            log.warn("[유효성 검사 실패] 이름 누락");
            throw new IllegalArgumentException("이름을 입력해 주세요.");
        }

        if (!request.getName().matches("^[가-힣a-zA-Z]+$")) {
            log.warn("[유효성 검사 실패] 이름 형식 오류: {}", request.getName());
            throw new IllegalArgumentException("이름은 한글 또는 영문만 입력 가능하며, 특수문자나 띄어쓰기는 사용할 수 없습니다.");
        }

        if (request.getPassword() == null || request.getPassword().isEmpty()) {
            log.warn("[유효성 검사 실패] 비밀번호 누락");
            throw new IllegalArgumentException("비밀번호를 입력해 주세요.");
        }

        // 유효성 검사: 권한 필수
        if (request.getAuthorities() == null || request.getAuthorities().trim().isEmpty()) {
            log.warn("[유효성 검사 실패] 권한 누락");
            throw new IllegalArgumentException("권한을 입력해 주세요.");
        }

    }

    // 관리자 정보 수정(이름, 메일검증여부)
    @Transactional
    public int updateAdmin(AdminUpdateRequest request) {
        log.info("[관리자 수정 시도] 이메일: {}", request.getEmail());

        // 유효성 검사
        validateRequest(request);

        // 테스트 계정 수정 불가
        if("admin@admin.com".equals(request.getEmail()) || "user@admin.com".equals(request.getEmail())) {
            log.warn("[관리자 수정 실패] 테스트 계정 수정 불가: {}", request.getEmail());
            throw new IllegalArgumentException("해당 이메일의 관리자는 수정할 수 없습니다.");
        }

        // 관리자 찾기
        Admin admin = adminRepository.findByEmail(request.getEmail()) // findById: id를 기준으로 1개의 데이터를 가져옴. Optional<User> 형태로 반환
                .orElseThrow(() -> {
                    log.warn("[관리자 수정 실패] 관리자 찾을 수 없음: {}", request.getEmail());
                    return new IllegalArgumentException("해당 이메일의 관리자를 찾을 수 없습니다."); // Optional의 orElseThrow: 데이터가 없는 경우(해당 id가 없는 경우) 예외를 던짐
                });

        // 비밀번호 암호화 적용
        String encodePassword = passwordEncoder.encode(request.getPassword());

        // 권한 변환 (문자열 -> 리스트)
        List<Authority> authorities;
        try {
            authorities = Collections.singletonList(Authority.valueOf(request.getAuthorities()));
        } catch (IllegalArgumentException e) {
            log.warn("[관리자 수정 실패] 권한 변환 실패: {}", request.getAuthorities());
            throw new IllegalArgumentException("유효한 권한을 입력해주세요.");
        }

        admin.updateAdmin(request.getName(), encodePassword, authorities, request.getEmailVerified()); // 객체 이름 변경

        log.info("[관리자 수정 성공] 이메일: {}", request.getEmail());
        return 1;
    }

    @Transactional
    public int deleteAdmin(String email) {
        log.info("[관리자 삭제 시도] 이메일: {}", email);

        // 테스트 계정 삭제 불가
        if("admin@admin.com".equals(email) || "user@admin.com".equals(email)) {
            log.warn("[관리자 삭제 실패] 테스트 계정 삭제 불가: {}", email);
            throw new IllegalArgumentException("해당 이메일의 관리자는 삭제할 수 없습니다.");
        }

        Admin admin = adminRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.warn("[관리자 삭제 실패] 관리자 찾을 수 없음: {}", email);
                    return new IllegalArgumentException("해당 이메일의 관리자를 찾을 수 없습니다.");
                });

        adminRepository.delete(admin);
        log.info("[관리자 삭제 성공] 이메일: {}", email);
        return 1;
    }

}
