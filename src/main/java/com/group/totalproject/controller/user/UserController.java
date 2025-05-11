package com.group.totalproject.controller.user;

import com.group.totalproject.dto.user.request.UserCreateRequest;
import com.group.totalproject.dto.user.request.UserDeleteRequest;
import com.group.totalproject.dto.user.request.UserUpdateRequest;
import com.group.totalproject.dto.user.response.UserResponse;
import com.group.totalproject.service.user.UserServiceV2;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@RestController // @RestController: 1.API 진입지점 만들어 줌 / 2.UserController 클래스를 스프링 빈으로 등록시켜 줌 / 3.@Controller + @ResponseBody(json 형태로 데이터를 반환해 줌)
public class UserController { // Controller: API와 HTTP 담당

    private final UserServiceV2 userService;
    private final CacheManager cacheManager;
    private final StringRedisTemplate redisTemplate;

    public UserController(UserServiceV2 userService, CacheManager cacheManager, StringRedisTemplate redisTemplate) {  // UserController가 JdbcTemplate에 의존
        this.userService = userService;
        this.cacheManager = cacheManager;
        this.redisTemplate = redisTemplate;
    }

    @PostMapping("/user") // 등록
    public ResponseEntity<?> saveUser(@RequestBody UserCreateRequest request) { // @RequestBody: HTTP 요청의 바디에 담긴 값들을 자바객체로 변환시켜 객체에 저장
        try {
            userService.saveUser(request, request.getPageSize());
            return ResponseEntity.ok("회원 등록이 완료되었습니다.");
        } catch (IllegalArgumentException e) {
            log.warn("회원 등록 실패: {}", e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/user") // 목록보기
    @PreAuthorize("hasAuthority('ROLE_ADMIN')") // @PreAuthorize("hasAuthority('ROLE_ADMIN')"): Spring Security의 어노테이션, ROLE_ADMIN 권한이 있어야 접근 가능.
    public ResponseEntity<List<UserResponse>> getUsers(
            @RequestParam(name = "cursor", required = false) Long cursor,
            @RequestParam(name = "page", required = false, defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "100") int size,
            @RequestParam(name = "type", defaultValue = "cache-cursor")String type
    ) {
        // log.info("[회원 목록 조회 요청] type: {}, cursor: {}, page: {}, size: {}", type, cursor, page, size);

        List<UserResponse> users;
        HttpHeaders headers = new HttpHeaders();
        String key = "";
        boolean isHit = false;

        long start = System.currentTimeMillis();

        switch (type) {
            case "cache-cursor":
                key = "users:cursor:" + (cursor != null ? cursor : "0") + ":size:" + size;
                Cache cache = cacheManager.getCache("getUsers");

                if (cache != null && cache.get(key) != null) {
                    isHit = true;
                    log.info("[캐시 HIT] key: {}", key);
                } else {
                    log.info("[캐시 MISS] key: {}", key);
                }

                users = userService.getUsersWithCache(cursor, size);
                break;

            case "cursor":
                log.info("[커서 기반 조회] cursor: {}, size: {}", cursor, size);
                users = userService.getUsersWithCursor(cursor, size);
                break;

            case "offset":
                log.info("[오프셋 기반 조회] page: {}, size: {}", page, size);
                users = userService.getUsersWithOffset(page, size);
                break;

            default:
                log.warn("잘못된 type 요청: {}", type);
                throw new IllegalArgumentException("Invalid type: " + type);
        }

        long duration = System.currentTimeMillis() - start;

        headers.add("X-Cache", isHit ? "HIT" : "MISS");
        headers.add("X-Response-Time", duration + "ms");

        if (isHit) {
            Long ttl = redisTemplate.getExpire("getUsers::" + key, TimeUnit.SECONDS);
            headers.add("X-TTL", ttl != null && ttl > 0 ? ttl.toString() : "No TTL");
        }

        log.info("[회원 목록 조회 완료] 응답 시간: {}ms, 조회 회원수: {}명", duration, users.size());
        return new ResponseEntity<>(users, headers, HttpStatus.OK);

/*
        1. CURCOR 기반 페이징 적용
        // API 조회 시작 시간
        long start = System.currentTimeMillis();

        // 캐시 히트 여부 조회를 위한 캐시의 키 생성
        String key = "users:cursor:" + (cursor != null ? cursor : "0") + ":size:" + size;

        // 캐시 히트 여부 판단
        boolean isHit = false;
        Cache cache = cacheManager.getCache("getUsers"); // "getUsers" 캐시를 가져옴
        if (cache != null && cache.get(key) != null) {  // 캐시에 해당 키가 존재하는지 확인
            isHit = true;
        }

        List<UserResponse> users = userService.getUsers(cursor, size);

        // API 조회 끝 시간
        long end = System.currentTimeMillis();

        // API 처리에 걸린 시간 계산
        long duration = end - start;

        // 응답 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Cache", isHit ? "HIT" : "MISS"); // 캐시 히트/미스 헤더
        headers.add("X-Response-Time", duration + "ms"); // API 응답 시간 헤더

        // === TTL 확인 (캐시 히트 시에만) ===
        if (isHit) {
            Long ttl = redisTemplate.getExpire("getUsers::" + key, TimeUnit.SECONDS); // getExpire: 레디스에서 특정 키의 TTL을 가져옴, TimeUnit.SECONDS: 반환되는 TTL을 초 단위로 설정
            headers.add("X-TTL", ttl != null && ttl > 0 ? ttl.toString() : "No TTL");
        }

        return new ResponseEntity<>(users, headers, HttpStatus.OK);
*/

/*
        2. OFFSET 기반 페이징 적용
        long start = System.currentTimeMillis();

        // 캐시는 사용하지 않으므로 MISS로 고정
        boolean isHit = false;

        List<UserResponse> users = userService.getUsers(cursor, size);

        long end = System.currentTimeMillis();
        long duration = end - start;

        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Cache", "MISS");
        headers.add("X-Response-Time", duration + "ms");

        return new ResponseEntity<>(users, headers, HttpStatus.OK);
*/



        /* 3. 초창기 방식: 컬렉션 순회하며 변환
        List<UserResponse> responses = new ArrayList<>();
        for (int i = 0; i < users.size(); i++) {
            responses.add(new UserResponse(i+1, users.get(i)));
        }
        return responses;
        */

        /* 4. JDBC + RowMapper
        return jdbcTemplate.query(sql, new RowMapper<UserResponse>() { // sql 결과들을 UserResponse 객체로 반환, RowMapper는 함수형 인터페이스
            @Override
            public UserResponse mapRow(ResultSet rs, int rowNum) throws SQLException { // mapROW: sql 결과를 UserResponse 객체로 매핑하여 결과를 리턴
                long id = rs.getLong("id");
                String name = rs.getString("name");
                int age = rs.getInt("age");
                return new UserResponse(id, name, age);
            }
        });
        */
        
        /* 5. JDBC + 람다식: 코드 간결화
        String sql = "SELECT * FROM user";
        return jdbcTemplate.query(sql, (rs, rowNum) -> { // 두번째 익명 클래스(익명 객체)를 람다식으로 변환
            long id = rs.getLong("id");
            String name = rs.getString("name");
            int age = rs.getInt("age");
            return new UserResponse(id, name, age);
        });
        */
    }

    @PutMapping("/user") // 수정
    public ResponseEntity<?> updateUser(@RequestBody UserUpdateRequest request) { // 객체화
        try {
            userService.updateUser(request);
            return ResponseEntity.ok("회원 이름이 수정되었습니다.");
        } catch (IllegalArgumentException e) {
            log.warn("회원 수정 실패: {}", e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/user") // 삭제
    public ResponseEntity<?> deleteUser(@RequestBody UserDeleteRequest request) {
        try {
            userService.deleteUser(request.getName(), request.getPageSize());
            return ResponseEntity.ok("회원이 삭제되었습니다.");
        } catch (IllegalArgumentException e) {
            log.warn("회원 삭제 실패: {}", e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/user/count")
    public ResponseEntity<Long> getUserCount() {
        return ResponseEntity.ok(userService.getUserCount());
    }

}
