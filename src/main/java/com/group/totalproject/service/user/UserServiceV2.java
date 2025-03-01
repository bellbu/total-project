package com.group.totalproject.service.user;

import com.group.totalproject.domain.user.User;
import com.group.totalproject.domain.user.UserRepository;
import com.group.totalproject.domain.user.loanhistory.UserLoanHistoryRepository;
import com.group.totalproject.dto.user.request.UserCreateRequest;
import com.group.totalproject.dto.user.request.UserUpdateRequest;
import com.group.totalproject.dto.user.response.UserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

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
    public void saveUser(UserCreateRequest request, int pageSize) { // 유저 저장 기능

        // 이름 검증: null 또는 빈 문자열일 경우 예외 처리
        if (request.getName() == null || request.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("이름은 필수입니다.");
        }

        // 이름 검증: 영어 또는 한글로 시작하고, 뒤에 숫자가 올 수 있는 패턴만 허용
        String nameRegex = "^[a-zA-Z가-힣]+[0-9]*$";
        if (!request.getName().matches(nameRegex)) {
            throw new IllegalArgumentException("이름은 영어 또는 한글로 시작해야 하며, 숫자를 포함할 수 있습니다. \n단, 띄워쓰기는 사용할 수 없습니다.");
        }

        // 이름 중복 검사
        if (userRepository.existsByName(request.getName())) {
            throw new IllegalArgumentException("이미 존재하는 이름입니다. \n다른 이름을 사용해주세요.");
        }

        // 나이 검증: null, 숫자가 아닌 값, 또는 음수일 경우 예외 처리
        if (request.getAge() == null) {
            throw new IllegalArgumentException("나이는 필수입니다.");
        }

        try {
            int age = Integer.parseInt(request.getAge().toString());
            if (age < 1) {
                throw new IllegalArgumentException("나이는 1 이상의 숫자여야 합니다.");
            }
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("나이는 숫자만 입력 가능합니다.");
        }

        // ✅ DB 등록 후 객체 생성
        User newUser = userRepository.save(new User(request.getName(), request.getAge()));

        // ✅ 첫 번째 페이지 캐시 키
        String firstPageCacheKey = "getUsers::users:cursor:0:size:" + pageSize;

        // ✅ 기존 캐시 조회
        List<UserResponse> cachedUsers = redisTemplate.opsForValue().get(firstPageCacheKey);

        // ✅ 조회된 캐시 데이터가 없으면 DB에만 저장 후 종료
        if (cachedUsers == null || cachedUsers.isEmpty()) {
            return;
        }

        // ✅ 조회된 캐시 데이터가 있을 경우 새로운 회원을 맨 앞에 추가
        List<UserResponse> updatedUsers = new ArrayList<>(cachedUsers);
        updatedUsers.add(0, new UserResponse(newUser));

        // ✅ 기존 TTL 가져오기 (초 단위)
        Long ttlInSeconds = redisTemplate.getExpire(firstPageCacheKey);

        // ✅ Long → Duration 변환 (TTL 유지)
        Duration currentTTL = (ttlInSeconds != null && ttlInSeconds > 0)
                ? Duration.ofSeconds(ttlInSeconds)
                : Duration.ofMinutes(5); // 기본 TTL 5분

        // ✅ TTL 유지하면서 캐시 저장
        redisTemplate.opsForValue().set(firstPageCacheKey, updatedUsers, currentTTL);

        // ✅ 역 인덱스 저장
        indexRedisTemplate.opsForSet().add("userCacheIndex::" + newUser.getId(), firstPageCacheKey);
    }

    // @Cacheable: 메서드 실행 결과를 캐시에 저장
    // Cache Aside 전략으로 캐싱 적용 (cacheNames: 캐시 이름을 설정 / key: Redis에 저장할 Key의 이름을 설정(#변수: 매개변수 값) / cacheManager: RedisCacheConfig에서 사용할 cacheManager의 Bean 이름을 지정)
    @Cacheable(cacheNames = "getUsers", key = "'users:cursor:' + (#cursor ?: '0') + ':size:' + #size", cacheManager = "userCacheManager")
    @Transactional(readOnly = true)
    public List<UserResponse> getUsers(Long cursor, int size) {
        Pageable pageable = PageRequest.of(0, size);  // ✅ Pageable 객체 생성
        List<User> users;

        if (cursor == null) {
            users = userRepository.findTopByOrderByIdDesc(pageable);
        } else {
            users = userRepository.findByIdLessThanOrderByIdDesc(cursor, pageable);
        }

        return users.stream()
                .map(UserResponse::new)
                .collect(Collectors.toList());

        /*
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"));
        return userRepository.findAll(pageRequest) // 조회된 사용자 데이터를 List<User> 형태로 가져옴
                .getContent()
                .stream()
                //.map(user -> new UserResponse(user.getId(), user.getName(), user.getAge()))
                .map(UserResponse::new) // map(): 스트림 요소를 사용하려는 형태로 변환하는 중간연산 / UserResponse::new(생성자 참조, (user) -> new UserResponse(user);와 동일한 형태) : User 객체를 UserResponse 객체로 변환
                .collect(Collectors.toList());  // UserResponse 객체들을 다시 리스트 형태로 수집하는 최종연산
        */
    }

    @Transactional
    public void updateUser(UserUpdateRequest request) {

        // 1. 이름 검증: null 또는 빈 문자열일 경우 예외 처리
        if (request.getName() == null || request.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("이름은 필수입니다.");
        }

        // 2. 이름 검증: 영어 또는 한글로 시작하고, 뒤에 숫자가 올 수 있는 패턴만 허용
        String nameRegex = "^[a-zA-Z가-힣]+[0-9]*$";
        if (!request.getName().matches(nameRegex)) {
            throw new IllegalArgumentException("이름은 영어 또는 한글로 시작하고, \n뒤에 숫자를 포함할 수 있습니다.");
        }

        // 3. 이름 중복 검사
        if (userRepository.existsByName(request.getName())) {
            throw new IllegalArgumentException("이미 사용 중인 이름입니다.");
        }

        // 4. 기존 회원 정보 가져오기
        User user = userRepository.findById(request.getId()) // findById: id를 기준으로 1개의 데이터를 가져옴. Optional<User> 형태로 반환
                .orElseThrow(() -> new IllegalArgumentException("해당 사용자가 존재하지 않습니다. ID: " + request.getId())); // Optional의 orElseThrow: 데이터가 없는 경우(해당 id가 없는 경우) 예외를 던짐

        // 4. 객체 이름 변경
        user.updateName(request.getName());
    }

    @Transactional
    public void deleteUser(String name, int pageSize) {
        User user = userRepository.findByName(name)
                .orElseThrow(() -> new IllegalArgumentException("해당 사용자가 존재하지 않습니다."));

        // 회원의 대출 기록 중 반납되지 않은 책이 있는지 확인
        if (userLoanHistoryRepository.existsByUserIdAndIsReturnFalse(user.getId())) {
            throw new IllegalArgumentException("대출 중인 회원은 삭제할 수 없습니다. \n반납 후 다시 시도해주세요.");
        }

        // ✅ 회원 삭제 (DB 먼저 삭제)
        userRepository.delete(user);

        // ✅ Redis에서 해당 회원이 포함된 캐시 삭제 (Look Aside)
        Set<String> cacheKeys = redisTemplate.keys("getUsers::users:cursor:*:size:" + pageSize);
        if (cacheKeys != null) {
            for (String cacheKey : cacheKeys) {
                String[] parts = cacheKey.split(":");
                System.out.println("파트 : "+Arrays.toString(parts));

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

                // ✅ id < cursor <= id + pageSize 조건 확인
                if (cursor == 0 || cursor > user.getId() && cursor <= user.getId() + pageSize) {
                    System.out.println("필터링된 캐시키: " + cacheKey);
                    List<UserResponse> cachedUsers = redisTemplate.opsForValue().get(cacheKey);
                    if (cachedUsers != null && cachedUsers.removeIf(u -> Long.valueOf(u.getId()).equals(user.getId()))) {
                        // ✅ 기존 TTL 유지 (만료 시간이 0보다 클 경우 유지)
                        Long ttlInSeconds = redisTemplate.getExpire(cacheKey);
                        Duration currentTTL = (ttlInSeconds != null && ttlInSeconds > 0)
                                ? Duration.ofSeconds(ttlInSeconds)
                                : Duration.ofMinutes(5); // 기본 TTL 5분

                        // ✅ 캐시 업데이트 (삭제된 회원이 반영된 리스트 저장)
                        redisTemplate.opsForValue().set(cacheKey, cachedUsers, currentTTL);
                    }
                }
            }
        }

        /*
        // ✅ 회원 삭제 (DB 먼저 삭제)
        userRepository.delete(user);

        // ✅ Redis에서 해당 회원이 포함된 캐시 삭제 (Look Aside)
        Set<String> cacheKeys = redisTemplate.keys("getUsers::users:cursor:*:size:"+pageSize);
        if (cacheKeys != null) {
            for (String cacheKey : cacheKeys) {
                System.out.println("캐시키 : "+cacheKey);
                List<UserResponse> cachedUsers = redisTemplate.opsForValue().get(cacheKey);
                if (cachedUsers != null && cachedUsers.removeIf(u -> Long.valueOf(u.getId()).equals(user.getId()))) {                    // ✅ 기존 TTL 유지 (만료 시간이 0보다 클 경우 유지)
                    Long ttlInSeconds = redisTemplate.getExpire(cacheKey);
                    Duration currentTTL = (ttlInSeconds != null && ttlInSeconds > 0)
                            ? Duration.ofSeconds(ttlInSeconds)
                            : Duration.ofMinutes(5); // 기본 TTL 5분

                    // ✅ 캐시 업데이트 (삭제된 회원이 반영된 리스트 저장)
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
