package com.group.totalproject.controller.user;

import com.group.totalproject.dto.user.request.UserCreateRequest;
import com.group.totalproject.dto.user.request.UserUpdateRequest;
import com.group.totalproject.dto.user.response.UserResponse;
import com.group.totalproject.service.user.UserServiceV2;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController // @RestController: 1.API 진입지점 만들어 줌 / 2.UserController 클래스를 스프링 빈으로 등록시켜 줌 / 3.@Controller + @ResponseBody(json 형태로 데이터를 반환해 줌)
public class UserController { // Controller: API와 HTTP 담당

    private final UserServiceV2 userService;

    public UserController(UserServiceV2 userService) {  // UserController가 JdbcTemplate에 의존
        this.userService = userService;
    }

    @PostMapping("/user") // 등록
    public ResponseEntity<?> saveUser(@RequestBody UserCreateRequest request) { // @RequestBody: HTTP 요청의 바디에 담긴 값들을 자바객체로 변환시켜 객체에 저장
        try {
            userService.saveUser(request, request.getPageSize());
            return ResponseEntity.ok("회원 등록이 완료되었습니다.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/user") // 목록보기
    @PreAuthorize("hasAuthority('ROLE_ADMIN')") // 'ROLE_' 접두사를 포함한 전체 권한명 사용
    public ResponseEntity<List<UserResponse>> getUsers(
        @RequestParam(required = false) Long cursor, @RequestParam(defaultValue = "1000") int size
    ) {
        return ResponseEntity.ok(userService.getUsers(cursor, size));

        /* 첫번째
        List<UserResponse> responses = new ArrayList<>();
        for (int i = 0; i < users.size(); i++) {
            responses.add(new UserResponse(i+1, users.get(i)));
        }
        return responses;
        */

        /* 두번째
        return jdbcTemplate.query(sql, new RowMapper<UserResponse>() { // sql 결과들을 UserResponse 객체로 반환, RowMapper는 함수형 인터페이스
            @Overrid    e
            public UserResponse mapRow(ResultSet rs, int rowNum) throws SQLException { // mapROW: sql 결과를 UserResponse 객체로 매핑하여 결과를 리턴
                long id = rs.getLong("id");
                String name = rs.getString("name");
                int age = rs.getInt("age");
                return new UserResponse(id, name, age);
            }
        });
        */
        
        /* 세번째
        String sql = "SELECT * FROM user";
        return jdbcTemplate.query(sql, (rs, rowNum) -> { // 두번째 익명 클래스(익명 객체)를 람다식으로 변환 RowMapper
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
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/user") // 삭제
    public ResponseEntity<?> deleteUser(@RequestParam("name") String name) {
        try {
            userService.deleteUser(name);
            return ResponseEntity.ok("회원이 삭제되었습니다.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/user/count")
    public ResponseEntity<Long> getUserCount() {
        return ResponseEntity.ok(userService.getUserCount());
    }

}
