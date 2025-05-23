package com.group.totalproject.service.user;

import com.group.totalproject.domain.user.User;
import com.group.totalproject.domain.user.UserRepository;
import com.group.totalproject.domain.user.loanhistory.UserLoanHistoryRepository;
import com.group.totalproject.dto.user.request.UserCreateRequest;
import com.group.totalproject.dto.user.request.UserUpdateRequest;
import com.group.totalproject.dto.user.response.UserResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor // final이 붙은 필드의 생성자를 자동으로 생성
public class UserServiceV2 {

    private final UserRepository userRepository; // UserRepository는 JpaRepository를 상속됨
    private final UserLoanHistoryRepository userLoanHistoryRepository;
    private final RedisTemplate<String, List<UserResponse>> redisTemplate;
    private final RedisTemplate<String, String> indexRedisTemplate; // 역 인덱스 저장용

    // 아래 있는 함수가 시작될 때 start transaction;을 해준다 (트랜잭션을 시작!)
    // 함수가 예외 없이 잘 끝났다면 commit;
    // 혹시라도 문제가 있다면 rollback; 단 IOException과 같은 Checked Exception은 롤백이 일어나지 않음
    @Transactional
    public void saveUser(UserCreateRequest request, int pageSize) {
        log.info("[회원 등록 요청] 이름: {}, 나이: {}", request.getName(), request.getAge());

        // 이름 검증: null 또는 빈 문자열일 경우 예외 처리
        if (request.getName() == null || request.getName().trim().isEmpty()) {
            log.warn("[이름이 비어 있음]");
            throw new IllegalArgumentException("이름은 필수입니다.");
        }

        // 이름 검증: 영어 또는 한글로 시작하고, 뒤에 숫자가 올 수 있는 패턴만 허용
        String nameRegex = "^[a-zA-Z가-힣]+[0-9]*$";
        if (!request.getName().matches(nameRegex)) {
            log.warn("[이름 형식이 유효하지 않음] 이름: {}", request.getName());
            throw new IllegalArgumentException("이름은 영어 또는 한글로 시작해야 하며, 숫자를 포함할 수 있습니다. \n단, 띄워쓰기는 사용할 수 없습니다.");
        }

        // 이름 중복 검사
        if (userRepository.existsByName(request.getName())) {
            log.warn("[이름 중복 발생] 이름: {}", request.getName());
            throw new IllegalArgumentException("이미 존재하는 이름입니다. \n다른 이름을 사용해주세요.");
        }

        // 나이 검증: null, 숫자가 아닌 값, 또는 음수일 경우 예외 처리
        if (request.getAge() == null) {
            log.warn("[나이 null값]");
            throw new IllegalArgumentException("나이는 필수입니다.");
        }

        try {
            int age = Integer.parseInt(request.getAge().toString());
            if (age < 1 || age > 999) {
                log.warn("[나이 범위 초과] 나이: {}", age);
                throw new IllegalArgumentException("나이는 1부터 999까지의 숫자만 입력할 수 있습니다.");
            }
        } catch (NumberFormatException e) {
            log.warn("[나이 형식 오류] 나이: {}", request.getAge());
            throw new IllegalArgumentException("나이는 숫자만 입력 가능합니다.");
        }

        // DB 등록 후 newUser 객체 생성
        User newUser = userRepository.save(new User(request.getName(), request.getAge()));
        log.info("[회원 등록 완료]");

        // 첫 번째 페이지 캐시 키 정의
        String firstPageCacheKey = "getUsers::users:cursor:0:size:" + pageSize;

        // 첫 번째 페이지 데이터를 Redis에서 조회
        List<UserResponse> cachedUsers = redisTemplate.opsForValue().get(firstPageCacheKey);

        // 조회된 캐시 데이터가 없으면 종료
        if (cachedUsers == null || cachedUsers.isEmpty()) {
            log.info("[첫 페이지 캐시 없음 또는 비어 있음, 캐시 갱신 생략]");
            return;
        }

        List<UserResponse> updatedUsers = new ArrayList<>(cachedUsers); // 조회된 캐시 데이터(cachedUsers)를 새 리스트(updatedUsers)에 복사
        updatedUsers.add(0, new UserResponse(newUser)); // 새로운 사용자(newUser)를 리스트 맨 앞에 추가
        log.info("[새 사용자 캐시 리스트에 추가 완료]");

        // 기존 만료시간(TTL, 초단위) 조회
        Long ttlInSeconds = redisTemplate.getExpire(firstPageCacheKey);

        // 기존 만료시간 존재하면 기존 유지, 존재하지 않으면 3분 기본 설정
        Duration currentTTL = (ttlInSeconds != null && ttlInSeconds > 0)
                ? Duration.ofSeconds(ttlInSeconds)
                : Duration.ofMinutes(3);

        // 새로운 데이터로 Redis 캐시 갱신 - set(key, value, duration)
        redisTemplate.opsForValue().set(firstPageCacheKey, updatedUsers, currentTTL);

    }

    // CACHE + CURSOR 기반 페이징 적용
    // @Cacheable: 메서드 실행 결과를 캐시에 저장
    // Cache Aside 전략으로 캐싱 적용 (cacheNames: 캐시 이름을 설정 / key: Redis에 저장할 Key의 이름을 설정(#변수: 매개변수 값) / cacheManager: RedisCacheConfig에서 사용할 cacheManager의 Bean 이름을 지정)
    @Cacheable(cacheNames = "getUsers", key = "'users:cursor:' + (#cursor ?: '0') + ':size:' + #size", cacheManager = "userCacheManager")
    @Transactional(readOnly = true) // 읽기 전용 트랜잭션
    public List<UserResponse> getUsersWithCache(Long cursor, int size) {
        return getUsersByCursor(cursor, size);
    }

    // CURSOR 기반 페이징 적용
    @Transactional(readOnly = true) // 읽기 전용 트랜잭션
    public List<UserResponse> getUsersWithCursor(Long cursor, int size) {
        return getUsersByCursor(cursor, size);
    }

    private List<UserResponse> getUsersByCursor(Long cursor, int size) {
        // Pageable 객체 생성(JPA 페이징 처리 객체): 한 번에 몇 개의 데이터를 가져올지 (LIMIT ?) 설정하는 역할
        Pageable pageable = PageRequest.of(0, size);  // PageRequest.of(0, size): 0 - 조회 페이지 번호, size - 페이지 당 로우 개수
        List<User> users;

        if (cursor == null) { // 처음 조회(첫 페이지 조회)할 때 최신 회원 데이터 size개 가져옴
            users = userRepository.findTopByOrderByIdDesc(pageable);
        } else {
            users = userRepository.findByIdLessThanOrderByIdDesc(cursor, pageable); // IdLessThan: id < cursor 조건을 의미
        }

        // List<User> → List<UserResponse> 변환
        return users.stream() // .stream(): 리스트를 스트림으로 변환(데이터를 하나씩 처리할 수 있는 형태로 변환)
                .map(UserResponse::new) // .map(): 스트림의 각 요소를 다른 값으로 변환할 때 사용 / UserResponse::new 는 user -> new UserResponse(user)와 같은 의미
                .collect(Collectors.toList()); // 변환된 스트림을 다시 리스트로 변환
    }

    // OFFSET 기반 페이징 적용
    @Transactional(readOnly = true) // 읽기 전용 트랜잭션
    public List<UserResponse> getUsersWithOffset(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"));

        Page<User> usersPage = userRepository.findAll(pageable); // 조회된 사용자 데이터를 List<User> 형태로 가져옴

        // List<User> → List<UserResponse> 변환 후 반환
        return usersPage.getContent().stream()
                .map(UserResponse::new)
                .collect(Collectors.toList()); // UserResponse 객체들을 다시 리스트 형태로 수집하는 최종연산

    }

/*
    // @Cacheable: 메서드 실행 결과를 캐시에 저장
    // Cache Aside 전략으로 캐싱 적용 (cacheNames: 캐시 이름을 설정 / key: Redis에 저장할 Key의 이름을 설정(#변수: 매개변수 값) / cacheManager: RedisCacheConfig에서 사용할 cacheManager의 Bean 이름을 지정)
    @Cacheable(cacheNames = "getUsers", key = "'users:cursor:' + (#cursor ?: '0') + ':size:' + #size", cacheManager = "userCacheManager")
    @Transactional(readOnly = true) // 읽기 전용 트랜잭션
    public List<UserResponse> getUsers(Long cursor, int size) {

        // CURSOR 기반 페이징 적용
        // Pageable 객체 생성(JPA 페이징 처리 객체): 한 번에 몇 개의 데이터를 가져올지 (LIMIT ?) 설정하는 역할
        Pageable pageable = PageRequest.of(0, size);  // PageRequest.of(0, size): 0 - 조회 페이지 번호, size - 페이지 당 로우 개수

        List<User> users;

        if (cursor == null) { // 처음 조회(첫 페이지 조회)할 때 최신 회원 데이터 size개 가져옴
            users = userRepository.findTopByOrderByIdDesc(pageable);
        } else {
            users = userRepository.findByIdLessThanOrderByIdDesc(cursor, pageable); // IdLessThan: id < cursor 조건을 의미
        }

        // List<User> → List<UserResponse> 변환
        return users.stream() // .stream(): 리스트를 스트림으로 변환(데이터를 하나씩 처리할 수 있는 형태로 변환)
                .map(UserResponse::new) // .map(): 스트림의 각 요소를 다른 값으로 변환할 때 사용 / UserResponse::new 는 user -> new UserResponse(user)와 같은 의미
                .collect(Collectors.toList()); // 변환된 스트림을 다시 리스트로 변환

*/
/*
        // OFFSET 기반 페이징 적용
        Pageable pageable = PageRequest.of(0, size, Sort.by(Sort.Direction.DESC, "id"));

        Page<User> usersPage = userRepository.findAll(pageable); // 조회된 사용자 데이터를 List<User> 형태로 가져옴

        // List<User> → List<UserResponse> 변환 후 반환
        return usersPage.getContent().stream()
                .map(UserResponse::new)
                .collect(Collectors.toList()); // UserResponse 객체들을 다시 리스트 형태로 수집하는 최종연산
*//*


    }
*/

    @Transactional
    public void updateUser(UserUpdateRequest request) {
        log.info("[회원 수정] 요청 ID: {}, 새 이름: {}", request.getId(), request.getName());

        // 1. 이름 검증: null 또는 빈 문자열일 경우 예외 처리
        if (request.getName() == null || request.getName().trim().isEmpty()) {
            log.warn("[이름이 비어있습니다]");
            throw new IllegalArgumentException("이름은 필수입니다.");
        }

        // 2. 이름 검증: 영어 또는 한글로 시작하고, 뒤에 숫자가 올 수 있는 패턴만 허용
        String nameRegex = "^[a-zA-Z가-힣]+[0-9]*$";
        if (!request.getName().matches(nameRegex)) {
            log.warn("[잘못된 이름 형식] 새 이름: {}", request.getName());
            throw new IllegalArgumentException("이름은 영어 또는 한글로 시작하고, \n뒤에 숫자를 포함할 수 있습니다.");
        }

        // 3. 이름 중복 검사
        if (userRepository.existsByName(request.getName())) {
            log.warn("[중복된 이름 사용 시도] 새 이름: {}", request.getName());
            throw new IllegalArgumentException("이미 사용 중인 이름입니다.");
        }

        // 4. 기존 회원 정보 가져오기
        User user = userRepository.findById(request.getId()) // findById: id를 기준으로 1개의 데이터를 가져옴. Optional<User> 형태로 반환
                .orElseThrow(() -> {
                    log.error("[수정 대상 회원를 찾을 수 없음] ID: {}", request.getId());
                    return new IllegalArgumentException("해당 사용자가 존재하지 않습니다. ID: " + request.getId()); // Optional의 orElseThrow: 데이터가 없는 경우(해당 id가 없는 경우) 예외를 던짐
                });

        // 5. 회원 이름 변경
        user.updateName(request.getName());
        log.info("[회원 이름 변경 완료]");

        // 6. Redis에서 해당 회원이 포함된 캐시만 찾아서 수정 수정된 회원의 데이터가 있는 캐시 키를 찾아서 해당 회원 수정 후 레디스에 저장
        Set<String> cacheKeys = redisTemplate.keys("getUsers::users:cursor:*:size:" + request.getPageSize()); // Redis에 저장된 캐시의 모든 Key값을 조회
        if (cacheKeys != null) {
            for (String cacheKey : cacheKeys) {
                List<UserResponse> cachedUsers = redisTemplate.opsForValue().get(cacheKey); // opsForValue(): key-value의 데이터를 다루기 위한 메서드 / get(key): 해당 키에 해당하는 value값을 가져오는 메소드
                if (cachedUsers != null) {
                    boolean updated = false;

                    for (int i = 0; i < cachedUsers.size(); i++) {
                        UserResponse u = cachedUsers.get(i);
                        if (Long.valueOf(u.getId()).equals(request.getId())) {
                            cachedUsers.set(i, new UserResponse(u.getId(), request.getName(), u.getAge())); // 캐시 데이터의 해당 회원 정보 수정
                            updated = true;
                        }
                    }

                    if (updated) {
                        Long ttlInSeconds = redisTemplate.getExpire(cacheKey);
                        Duration currentTTL = (ttlInSeconds != null && ttlInSeconds > 0)
                                ? Duration.ofSeconds(ttlInSeconds)
                                : Duration.ofMinutes(3);

                        redisTemplate.opsForValue().set(cacheKey, cachedUsers, currentTTL); // 수정된 리스트(cachedUsers)를 기존 TTL을 유지한 채로 다시 Redis에 저장
                    }
                }
            }
        }

/*
        // 6. Redis에서 해당 회원이 포함된 캐시만 찾아서 수정
        Set<String> cacheKeys = redisTemplate.keys("getUsers::users:cursor:*:size:" + request.getPageSize()); // 모든 캐시 키 조회
        if (cacheKeys != null) {
            for (String cacheKey : cacheKeys) {
                String[] parts = cacheKey.split(":"); // 캐시 키 ":" 기준으로 문자열 자른 후 배열에 저장

                if (parts.length < 5) { // cursor값 가져올 수 없는 해당 캐시키 건너뜀
                    System.out.println("잘못된 캐시 키 구조: " + cacheKey);
                    continue;
                }

                Long cursor;
                try {
                    cursor = Long.parseLong(parts[4]); // cursor 값 가져와서 Long 타입으로 변환
                } catch (NumberFormatException e) {
                    continue; // 변환 실패 시 해당 캐시 키 건너뜀
                }

                // 캐시 업데이트 조건: 첫 페이지 curosr나 회원이 포함될 cursor 값의 캐시키
                if (cursor == 0 || (cursor > request.getId() && cursor <= request.getId() + request.getPageSize())) {
                    List<UserResponse> cachedUsers = redisTemplate.opsForValue().get(cacheKey); // 조건 맞는 캐시 데이터(List<UserResponse>) 조회
                    if (cachedUsers != null) {

                        cachedUsers.replaceAll(u ->
                                u.getId() == request.getId()
                                        ? new UserResponse(u.getId(), request.getName(), u.getAge()) // 수정할 회원은 새로운 UserResponse 객체로 이름 변경
                                        : u // 나머지 회원은 그대로 유지
                        );

                        Long ttlInSeconds = redisTemplate.getExpire(cacheKey); // 해당 캐시 데이터 만료 시간(TTL) 조회
                        Duration currentTTL = (ttlInSeconds != null && ttlInSeconds > 0)
                                ? Duration.ofSeconds(ttlInSeconds) // 기존 만료시간(TTL) 존재하면 유지
                                : Duration.ofMinutes(3); // 만료시간(TTL) 존재하지 않으면 3분 설정

                        // 변경된 회원 목록 Redis에 반영 - set(key, value, duration)
                        redisTemplate.opsForValue().set(cacheKey, cachedUsers, currentTTL);
                    }
                }
            }
        }
*/

    }

    @Transactional
    public void deleteUser(String name, int pageSize) {
        log.info("[회원 삭제 요청] 이름: {}, 페이지 크기: {}", name, pageSize);

        // 1. 회원 유무 조회
        User user = userRepository.findByName(name)
                .orElseThrow(() -> {
                    log.error("[해당 이름의 사용자가 존재하지 않음] 이름: {}", name);
                    return new IllegalArgumentException("존재하지 않는 사용자입니다.");
                });

        // 2. 회원의 대출 기록 중 반납되지 않은 책이 있는지 확인
        boolean hasUnreturnedBooks = userLoanHistoryRepository.existsByUserIdAndIsReturnFalse(user.getId());
        if (hasUnreturnedBooks) {
            log.warn("[삭제 불가 - 대출 중인 책이 있음] 사용자 ID: {}", user.getId());
            throw new IllegalArgumentException("대출 중인 회원은 삭제할 수 없습니다. \n반납 후 다시 시도해주세요.");
        }

        // 3. 회원 삭제
        userRepository.delete(user);
        log.info("[회원 삭제 완료]");

        // 4. 삭제된 회원의 데이터가 있는 캐시 키를 찾아서 해당 회원 삭제 후 레디스에 저장
        Set<String> cacheKeys = redisTemplate.keys("getUsers::users:cursor:*:size:" + pageSize); // Redis에 저장된 캐시의 모든 Key값을 조회
        if (cacheKeys != null) {
            for (String cacheKey : cacheKeys) {
                List<UserResponse> cachedUsers = redisTemplate.opsForValue().get(cacheKey); // opsForValue(): key-value의 데이터를 다루기 위한 메서드 / get(key): 해당 키에 해당하는 value값을 가져오는 메소드
                if (cachedUsers != null) {
                    boolean removed = cachedUsers.removeIf(u -> Long.valueOf(u.getId()).equals(user.getId())); // removeIf(): 조건에 맞는 요소를 리스트에서 제거하는 메소드(true/false로 반환)
                    if (removed) {
                        Long ttlInSeconds = redisTemplate.getExpire(cacheKey);
                        Duration currentTTL = (ttlInSeconds != null && ttlInSeconds > 0)
                                ? Duration.ofSeconds(ttlInSeconds)
                                : Duration.ofMinutes(3);

                        redisTemplate.opsForValue().set(cacheKey, cachedUsers, currentTTL); // 수정된 리스트(cachedUsers)를 기존 TTL을 유지한 채로 다시 Redis에 저장
                    }
                }
            }
        }

/*
        // 삭제된 회원이 포함된 캐시만 찾아서 수정
        Set<String> cacheKeys = redisTemplate.keys("getUsers::users:cursor:*:size:" + pageSize);
        if (cacheKeys != null) {
            for (String cacheKey : cacheKeys) {
                String[] parts = cacheKey.split(":");

                if (parts.length < 5) {
                    System.out.println("잘못된 캐시 키 구조: " + cacheKey);
                    continue;
                }

                Long cursor;
                try {
                    cursor = Long.parseLong(parts[4]);
                } catch (NumberFormatException e) {
                    continue;
                }

                if (cursor == 0 || cursor > user.getId() && cursor <= user.getId() + pageSize) {
                    List<UserResponse> cachedUsers = redisTemplate.opsForValue().get(cacheKey);
                    if (cachedUsers != null && cachedUsers.removeIf(u -> Long.valueOf(u.getId()).equals(user.getId()))) { // cachedUsers 리스트에서 삭제할 회원 리스트에서 제거
                        Long ttlInSeconds = redisTemplate.getExpire(cacheKey);
                        Duration currentTTL = (ttlInSeconds != null && ttlInSeconds > 0)
                                ? Duration.ofSeconds(ttlInSeconds)
                                : Duration.ofMinutes(3);

                        // 삭제된 회원이 반영된 리스트 Redis에 반영 - set(key, value, duration)
                        redisTemplate.opsForValue().set(cacheKey, cachedUsers, currentTTL);
                    }
                }
            }
        }
*/

        /*
        // 회원 삭제
        userRepository.delete(user);

        // Redis에서 해당 회원이 포함된 캐시 삭제 (Look Aside)
        Set<String> cacheKeys = redisTemplate.keys("getUsers::users:cursor:*:size:"+pageSize);
        if (cacheKeys != null) {
            for (String cacheKey : cacheKeys) {
                System.out.println("캐시키 : "+cacheKey);
                List<UserResponse> cachedUsers = redisTemplate.opsForValue().get(cacheKey);
                if (cachedUsers != null && cachedUsers.removeIf(u -> Long.valueOf(u.getId()).equals(user.getId()))) {                    // ✅ 기존 TTL 유지 (만료 시간이 0보다 클 경우 유지)
                    Long ttlInSeconds = redisTemplate.getExpire(cacheKey);
                    Duration currentTTL = (ttlInSeconds != null && ttlInSeconds > 0)
                            ? Duration.ofSeconds(ttlInSeconds)
                            : Duration.ofMinutes(3); // 기본 TTL 3분

                    // 캐시 업데이트 (삭제된 회원이 반영된 리스트 저장)
                    redisTemplate.opsForValue().set(cacheKey, cachedUsers, currentTTL);
                }
            }
        }
        */

        /*
        // 회원의 대출 기록 중 반납되지 않은 책이 있는지 확인
        User user = userRepository.findByName(name)
                .orElseThrow(IllegalArgumentException::new);

        if (userLoanHistoryRepository.existsByUserIdAndIsReturnFalse(user.getId())) {
            throw new IllegalArgumentException("대출 중인 회원은 삭제할 수 없습니다. \n반납 후 다시 시도해주세요.");
        }

        userRepository.delete(user);
        */

    }

    @Transactional(readOnly = true)
    public long getUserCount() {
        return userRepository.count(); // JpaRepository에서 기본 제공하는 메서드
    }
}
