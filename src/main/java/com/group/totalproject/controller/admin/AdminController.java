package com.group.totalproject.controller.admin;

import com.group.totalproject.domain.admin.Admin;
import com.group.totalproject.dto.admin.request.AdminCreateRequest;
import com.group.totalproject.dto.admin.request.AdminUpdateRequest;
import com.group.totalproject.security.custom.CustomAdmin;
import com.group.totalproject.service.admin.AdminService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;


/*
* [GET]     /admin/info - 회원정보 조회   (ROLE_ADMIN, ROLE_USER)
* [POST]    /admin      - 회원가입        ALL
* [PUT]     /admin      - 회원정보 수정   (ROLE_ADMIN, ROLE_USER)
* [DELETE]  /admin      - 회원탈퇴       (ROLE_ADMIN, ROLE_USER)
* */
@Slf4j // log 객체를 자동 생성
@RequiredArgsConstructor // 필드 주입을 생성자 주입으로 자동 설정
@RestController
@RequestMapping("/admin")
public class AdminController { // JWT 토큰 생성 RestController

    private final AdminService adminService;

    /**
     * 관리자 조회
     */
    @Secured({"ROLE_ADMIN", "ROLE_USER"}) // 해당 메서드에 접근할 수 있는 권한 설정
    @GetMapping("/info")
    public ResponseEntity<?> getAdmins(@AuthenticationPrincipal CustomAdmin customAdmin) { // @AuthenticationPrincipal: 로그인 인증 시 SecurityContextHolder에서 저장된 CustomAdmin 객체를 가져옴
        if (customAdmin == null) {
            log.warn("인증되지 않은 사용자가 관리자 정보를 요청함");
            throw new IllegalStateException("CustomAdmin is null");
        }

        Admin admin = customAdmin.getAdmin();

        // 인증된 사용자 정보
        if( admin != null )
            return new ResponseEntity<>(admin, HttpStatus.OK);

        log.warn("관리자 정보 없음");
        // 인증 되지 않음
        return new ResponseEntity<>("UNAUTHORIZED", HttpStatus.UNAUTHORIZED);
    }

    /**
     * 관리자 가입
     */
    @PostMapping("")
    public ResponseEntity<?> saveAdmin(@RequestBody AdminCreateRequest request) throws Exception {
        try {
            int result = adminService.saveAdmin(request);

            if( result > 0 ) {
                return ResponseEntity.ok("SUCCESS");
            } else {
                log.warn("관리자 등록 실패: {}", request.getEmail());
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("FAIL");
            }
        } catch (IllegalArgumentException e) {
            log.error("관리자 등록 중 예외 발생: {}", e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }


    }


    /**
     * 관리자 정보 수정
     */
    @Secured({"ROLE_ADMIN", "ROLE_USER"}) // ADMIN 권한 설정
    @PutMapping("")
    public ResponseEntity<?> updateAdmin(@RequestBody AdminUpdateRequest request) throws Exception {
        try {
            int result = adminService.updateAdmin(request);

            if( result > 0 ) {
                return ResponseEntity.ok("SUCCESS");
            } else {
                log.warn("관리자 정보 수정 실패: {}", request.getEmail());
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("FAIL");
            }
        } catch (IllegalArgumentException e) {
            log.error("관리자 정보 수정 중 예외 발생: {}", e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


    /**
     * 관리자 탈퇴
     */
    @Secured({"ROLE_ADMIN", "ROLE_USER"}) // Admin 권한 설정
    @DeleteMapping("/{email}")
    public ResponseEntity<?> destroy(@PathVariable("email") String email) throws Exception {
        try {

            int result = adminService.deleteAdmin(email);

            if( result > 0 ) {
                return new ResponseEntity<>("SUCCESS", HttpStatus.OK);
            }
            else {
                log.warn("관리자 탈퇴 실패: {}", email);
                return new ResponseEntity<>("FAIL", HttpStatus.BAD_REQUEST);
            }

        } catch (IllegalArgumentException e) {
            log.error("관리자 탈퇴 중 예외 발생: {}", e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }

    }

}
