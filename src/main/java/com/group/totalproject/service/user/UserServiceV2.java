package com.group.totalproject.service.user;

import com.group.totalproject.domain.user.User;
import com.group.totalproject.domain.user.UserRepository;
import com.group.totalproject.domain.user.loanhistory.UserLoanHistoryRepository;
import com.group.totalproject.dto.user.request.UserCreateRequest;
import com.group.totalproject.dto.user.request.UserUpdateRequest;
import com.group.totalproject.dto.user.response.UserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor // final이 붙은 필드의 생성자를 자동으로 생성
public class UserServiceV2 {

    private final UserRepository userRepository; // UserRepository는 JpaRepository를 상속됨
    private final UserLoanHistoryRepository userLoanHistoryRepository;

    // 아래 있는 함수가 시작될 때 start transaction;을 해준다 (트랜잭션을 시작!)
    // 함수가 예외 없이 잘 끝났다면 commit;
    // 혹시라도 문제가 있다면 rollback; 단 IOException과 같은 Checked Exception은 롤백이 일어나지 않음
    @Transactional
    public void saveUser(UserCreateRequest request) { // 유저 저장 기능

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

        userRepository.save(new User(request.getName(), request.getAge()));
    }

    @Transactional(readOnly = true)
    public List<UserResponse> getUsers(int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"));
        return userRepository.findAll(pageRequest) // 조회된 사용자 데이터를 List<User> 형태로 가져옴
                .getContent()
                .stream()
                //.map(user -> new UserResponse(user.getId(), user.getName(), user.getAge()))
                .map(UserResponse::new) // map(): 스트림 요소를 사용하려는 형태로 변환하는 중간연산 / UserResponse::new(생성자 참조, (user) -> new UserResponse(user);와 동일한 형태) : User 객체를 UserResponse 객체로 변환
                .collect(Collectors.toList());  // UserResponse 객체들을 다시 리스트 형태로 수집하는 최종연산
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
    public void deleteUser(String name) {

        User user = userRepository.findByName(name)
                .orElseThrow(IllegalArgumentException::new);

        // 회원의 대출 기록 중 반납되지 않은 책이 있는지 확인
        if (userLoanHistoryRepository.existsByUserIdAndIsReturnFalse(user.getId())) {
            throw new IllegalArgumentException("대출 중인 회원은 삭제할 수 없습니다. \n반납 후 다시 시도해주세요.");
        }

        userRepository.delete(user);

    }

    @Transactional(readOnly = true)
    public long getUserCount() {
        return userRepository.count(); // JpaRepository에서 기본 제공하는 메서드
    }
}
