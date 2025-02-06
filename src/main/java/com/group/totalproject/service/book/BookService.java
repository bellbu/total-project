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
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    @Transactional
    public void saveBook(BookCreateRequest request) {
        // 이름 검증: null 또는 빈 문자열일 경우 예외 처리
        if (request.getName() == null || request.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("책 이름은 필수입니다.");
        }

        if (bookRepository.existsByName(request.getName())){
            throw new IllegalArgumentException("이미 존재하는 책 이름입니다. \n다른 이름을 사용해주세요.");
        }

        bookRepository.save(new Book(request.getName()));
    }

    @Transactional
    public void loanBook(BookLoanRequest request) {
        
        // 회원명 검증: null 또는 빈 문자열일 경우 예외 처리
        if (request.getUserName() == null || request.getUserName().trim().isEmpty()) {
            throw new IllegalArgumentException("회원명은 필수입니다.");
        }

        // 책 이름 검증: null 또는 빈 문자열일 경우 예외 처리
        if (request.getBookName() == null || request.getBookName().trim().isEmpty()) {
            throw new IllegalArgumentException("책 이름은 필수입니다.");
        }

        // 1. 유저 정보 조회 (select * from user where user.name = ?)
        User user = userRepository.findByName(request.getUserName())
                .orElseThrow(() -> new IllegalArgumentException("등록되지 않은 회원입니다. \n 확인 후 다시 입력해주세요."));

        // 2. 책 정보 조회 (select * from book where book.name = ?)
        Book book = bookRepository.findByName(request.getBookName())
                .orElseThrow(() -> new IllegalArgumentException("등록되지 않은 책입니다. \n 확인 후 다시 입력해주세요."));


        // 3. 책 대출 기록 조회 (반납한 경우 true(1) => 책 대출, 반납되지 않은 경우(대출중인 경우) false(0) => 예외 처리)
        if (userLoanHistoryRepository.existsByBookIdAndIsReturn(book.getId(), false)) {
            throw new IllegalArgumentException("죄송합니다. \n해당 책은 현재 대출 중입니다.");
        }

        // 4. 책 대출
        user.loanBook(book);

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

        // 회원명 검증: null 또는 빈 문자열일 경우 예외 처리
        if (request.getUserName() == null || request.getUserName().trim().isEmpty()) {
            throw new IllegalArgumentException("회원명은 필수입니다.");
        }
        
        // 책 이름 검증: null 또는 빈 문자열일 경우 예외 처리
        if (request.getBookName() == null || request.getBookName().trim().isEmpty()) {
            throw new IllegalArgumentException("책 이름은 필수입니다.");
        }

        // 1. 유저 정보 조회 (select * from user where user.name = ?)
        Long userId = userRepository.findIdByName(request.getUserName())
                .orElseThrow(() -> new IllegalArgumentException("등록되지 않은 회원입니다. \n 확인 후 다시 입력해주세요."));

        // 2. 책 정보 조회 (select * from book where book.name = ?)
        Long bookId = bookRepository.findIdByName(request.getBookName())
                .orElseThrow(() -> new IllegalArgumentException("등록되지 않은 책입니다. \n 확인 후 다시 입력해주세요."));

        // 3. 해당 회원의 책 대출 기록 조회
        UserLoanHistory loanHistory = userLoanHistoryRepository
                .findByUserIdAndBookIdAndIsReturnFalse(userId, bookId)
                .orElseThrow(() -> new IllegalArgumentException("해당 책을 대출한 기록이 없습니다. \n 다시 확인해주세요."));

        loanHistory.doReturn();

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
