package com.group.totalproject.service.user;

import com.group.totalproject.domain.user.User;
import com.group.totalproject.domain.user.UserRepository;
import com.group.totalproject.dto.user.request.UserCreateRequest;
import com.group.totalproject.dto.user.request.UserUpdateRequest;
import com.group.totalproject.dto.user.response.UserResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserServiceV2 {

    private final UserRepository userRepository; // UserRepository는 JpaRepository를 상속됨

    public UserServiceV2(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // 아래 있는 함수가 시작될 때 start transaction;을 해준다 (트랜잭션을 시작!)
    // 함수가 예외 없이 잘 끝났다면 commit;
    // 혹시라도 문제가 있다면 rollback; 단 IOException과 같은 Checked Exception은 롤백이 일어나지 않음
    @Transactional
    public void saveUser(UserCreateRequest request) { // 유저 저장 기능
        // 이름 검증: null 또는 빈 문자열일 경우 예외 처리
        if (request.getName() == null || request.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("이름은 필수입니다.");
        }

        // 나이 검증: null, 숫자가 아닌 값, 또는 음수일 경우 예외 처리
        if (request.getAge() == null) {
            throw new IllegalArgumentException("나이는 필수입니다.");
        }

        try {
            int age = Integer.parseInt(request.getAge().toString());
            if (age < 0) {
                throw new IllegalArgumentException("나이는 0 이상의 숫자여야 합니다.");
            }
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("나이는 숫자만 입력 가능합니다.");
        }

        userRepository.save(new User(request.getName(), request.getAge()));
    }

    @Transactional(readOnly = true)
    public List<UserResponse> getUsers() {
        return userRepository.findAll().stream() // findAll(): 메소드는 저장소에서 모든 사용자 데이터를 List<User> 형태로 가져옴
                //.map(user -> new UserResponse(user.getId(), user.getName(), user.getAge()))
                .map(UserResponse::new) // map(): 스트림 요소를 사용하려는 형태로 변환하는 중간연산 / UserResponse::new(생성자 참조, (user) -> new UserResponse(user);와 동일한 형태) : User 객체를 UserResponse 객체로 변환
                .collect(Collectors.toList());  // UserResponse 객체들을 다시 리스트 형태로 수집하는 최종연산
    }
    @Transactional
    public void updateUser(UserUpdateRequest request) {
        User user = userRepository.findById(request.getId()) // findById: id를 기준으로 1개의 데이터를 가져옴. Optional<User> 형태로 반환
                .orElseThrow(IllegalArgumentException::new); // Optional의 orElseThrow: 데이터가 없는 경우(해당 id가 없는 경우) 예외를 던짐

        user.updateName(request.getName()); // 객체 이름 변경
    }

    @Transactional
    public void deleteUser(String name) {
        User user = userRepository.findByName(name)
                .orElseThrow(IllegalArgumentException::new);
        userRepository.delete(user);
    }
}
