package com.group.totalproject.service.book;

import com.group.totalproject.domain.book.Book;
import com.group.totalproject.domain.book.BookRepository;
import com.group.totalproject.domain.user.User;
import com.group.totalproject.domain.user.UserRepository;
import com.group.totalproject.domain.user.loanhistory.UserLoanHistory;
import com.group.totalproject.domain.user.loanhistory.UserLoanHistoryRepository;
import com.group.totalproject.dto.book.request.BookCreateRequest;
import com.group.totalproject.dto.book.request.BookLoanRequest;
import com.group.totalproject.dto.book.request.BookReturnRequest;
import com.group.totalproject.dto.book.response.LoanResponse;
import com.group.totalproject.dto.user.response.UserResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor // final이 붙은 필드의 생성자를 자동으로 생성
public class BookService {

    private final BookRepository bookRepository;
    private final UserLoanHistoryRepository userLoanHistoryRepository;
    private final UserRepository userRepository;

/*
    public BookService(
            BookRepository bookRepository,
            UserLoanHistoryRepository userLoanHistoryRepository,
            UserRepository userRepository
    ) {
        this.bookRepository = bookRepository;
        this.userLoanHistoryRepository = userLoanHistoryRepository;
        this.userRepository = userRepository;
    }
*/

    @Transactional(readOnly = true)
    public List<LoanResponse> getLoans() {
        return userLoanHistoryRepository.findAll(Sort.by(Sort.Direction.DESC, "id")).stream() // findAll(): 메소드는 저장소에서 모든 사용자 데이터를 List<LoanResponse> 형태로 가져옴
                .map(LoanResponse::new) // map(): 스트림 요소를 사용하려는 형태로 변환하는 중간연산 / LoanResponse::new(생성자 참조, (user) -> new LoanResponse(user);와 동일한 형태) : userLoanHistoryRepository 객체를 LoanResponse 객체로 변환
                .collect(Collectors.toList());  // LoanResponse 객체들을 다시 리스트 형태로 수집하는 최종연산
    }

    @Transactional
    public void saveBook(BookCreateRequest request) {
        log.info("[도서 등록 요청] 책이름: {}", request.getName());
        // 이름 검증: null 또는 빈 문자열일 경우 예외 처리
        if (request.getName() == null || request.getName().trim().isEmpty()) {
            log.warn("[도서 등록 실패] 이름 누락");
            throw new IllegalArgumentException("책 이름은 필수입니다.");
        }

        if (!request.getName().matches("^[^<>]*$")) {
            log.warn("[도서 등록 실패] 금지 문자 포함: {}", request.getName());
            throw new IllegalArgumentException("책 이름에 <, > 문자를 사용할 수 없습니다.");
        }

        if (bookRepository.existsByName(request.getName())){
            log.warn("[도서 등록 실패] 중복된 이름: {}", request.getName());
            throw new IllegalArgumentException("이미 존재하는 책 이름입니다. \n다른 이름을 사용해주세요.");
        }

        bookRepository.save(new Book(request.getName()));
        log.info("[도서 등록 완료]");
    }

    @Transactional
    public void loanBook(BookLoanRequest request) {
        log.info("[도서 대출 요청] 회원: {}, 책이름: {}", request.getUserName(), request.getBookName());
        
        // 회원명 검증: null 또는 빈 문자열일 경우 예외 처리
        if (request.getUserName() == null || request.getUserName().trim().isEmpty()) {
            log.warn("[도서 대출 실패] 회원명 누락");
            throw new IllegalArgumentException("회원명은 필수입니다.");
        }

        // 책 이름 검증: null 또는 빈 문자열일 경우 예외 처리
        if (request.getBookName() == null || request.getBookName().trim().isEmpty()) {
            log.warn("[도서 대출 실패] 책 이름 누락");
            throw new IllegalArgumentException("책 이름은 필수입니다.");
        }

        // 1. 유저 정보 조회 (select * from user where user.name = ?)
        User user = userRepository.findByName(request.getUserName())
                .orElseThrow(() -> {
                    log.warn("[도서 대출 실패] 존재하지 않는 회원: {}", request.getUserName());
                    return new IllegalArgumentException("등록되지 않은 회원입니다. \n 확인 후 다시 입력해주세요.");
                });

        // 2. 책 정보 조회 (select * from book where book.name = ?)
        Book book = bookRepository.findByName(request.getBookName())
                .orElseThrow(() -> {
                    log.warn("[도서 대출 실패] 존재하지 않는 도서: {}", request.getBookName());
                    return new IllegalArgumentException("등록되지 않은 책입니다. \n 확인 후 다시 입력해주세요.");
                });

        // 3. 책 대출 기록 조회 (반납한 경우 true(1) => 책 대출, 반납되지 않은 경우(대출중인 경우) false(0) => 예외 처리)
        if (userLoanHistoryRepository.existsByBookIdAndIsReturn(book.getId(), false)) {
            log.warn("[도서 대출 실패] 이미 대출 중인 책: {}", request.getBookName());
            throw new IllegalArgumentException("죄송합니다. \n해당 책은 현재 대출 중입니다.");
        }

        // 4. 책 대출
        user.loanBook(book);
        log.info("[도서 대출 성공]");

        /*
        // ※ 책 이름으로 히스토리 저장하는 경우
        if (userLoanHistoryRepository.existsByBookNameAndIsReturn(book.getName(), false)) {
            throw new IllegalArgumentException("진작 대출되어 있는 책입니다.");
        }
        */

        /*
        // ※ 책 이름으로 히스토리 저장하는 경우
        user.loanBook(book.getName());
        */

        /*
        // 5. 유저 정보와 책 정보를 기반으로 UserLoanHistory를 저장
        userLoanHistoryRepository.save(new UserLoanHistory(user, book.getName()));
        */

    }

    @Transactional
    public void returnBook(BookReturnRequest request) {
        log.info("[도서 반납 요청] 회원: {}, 책이름: {}", request.getUserName(), request.getBookName());

        // 회원명 검증: null 또는 빈 문자열일 경우 예외 처리
        if (request.getUserName() == null || request.getUserName().trim().isEmpty()) {
            log.warn("[도서 반납 실패] 회원명 누락");
            throw new IllegalArgumentException("회원명은 필수입니다.");
        }
        
        // 책 이름 검증: null 또는 빈 문자열일 경우 예외 처리
        if (request.getBookName() == null || request.getBookName().trim().isEmpty()) {
            log.warn("[도서 반납 실패] 책 이름 누락");
            throw new IllegalArgumentException("책 이름은 필수입니다.");
        }

        // 1. 유저 정보 조회 (select * from user where user.name = ?)
        Long userId = userRepository.findIdByName(request.getUserName())
                .orElseThrow(() -> {
                    log.warn("[도서 반납 실패] 존재하지 않는 회원: {}", request.getUserName());
                    return new IllegalArgumentException("등록되지 않은 회원입니다. \n 확인 후 다시 입력해주세요.");
                });

        // 2. 책 정보 조회 (select * from book where book.name = ?)
        Long bookId = bookRepository.findIdByName(request.getBookName())
                .orElseThrow(() -> {
                    log.warn("[도서 반납 실패} 존재하지 않는 도서: {}", request.getBookName());
                    return new IllegalArgumentException("등록되지 않은 책입니다. \n 확인 후 다시 입력해주세요.");
                });

        // 3. 해당 회원의 책 대출 기록 조회
        UserLoanHistory loanHistory = userLoanHistoryRepository
                .findByUserIdAndBookIdAndIsReturnFalse(userId, bookId)
                .orElseThrow(() -> {
                    log.warn("[도서 반납 실패] 대출 기록 없음: userId={}, bookId={}", userId, bookId);
                    return new IllegalArgumentException("해당 책을 대출한 기록이 없습니다. \n 다시 확인해주세요.");
                });

        loanHistory.doReturn();
        log.info("[도서 반납 성공]");

        /*
        user.returnBook(book);
        */
        
        /*
        // ※ 책 이름으로 히스토리 저장하는 경우
        user.returnBook(request.getBookName());
        */

        /*
        // ※ 책 이름으로 히스토리 저장하는 경우
        user.returnBook(request.getBookName());
        */

        /*
        // 2. 유저아이디와 책이름으로 대출 id를 가져온다
        UserLoanHistory history = userLoanHistoryRepository.findByUserIdAndBookName(user.getId(), request.getBookName())
                .orElseThrow(IllegalArgumentException::new);

        // 3. 대출id를 반납으로 업데이트 한다.
        history.doReturn();
        */
    }

}
