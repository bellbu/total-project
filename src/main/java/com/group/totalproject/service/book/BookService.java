package com.group.totalproject.service.book;

import com.group.totalproject.domain.book.Book;
import com.group.totalproject.domain.book.BookRepository;
import com.group.totalproject.domain.user.User;
import com.group.totalproject.domain.user.UserRepository;
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

        bookRepository.save(new Book(request.getName()));
    }

    @Transactional
    public void loanBook(BookLoanRequest request) {
        // 1. 책 정보를 가져온다
        Book book = bookRepository.findByName(request.getBookName())
        .orElseThrow(IllegalArgumentException::new);
        System.out.println("테스트232323232323 : "+book.toString());

        // 2. 대출기록 정보를 확인해서 대출중인지 확인합니다.(반납한 경우 true(1), 반납되지 않은 경우(대출중인 경우) false(0))
        // 3. 만약에 확인했는데 대출 중이라면 예외를 발생시킵니다.
        if (userLoanHistoryRepository.existsByBookIdAndIsReturn(book.getId(), false)) {
            throw new IllegalArgumentException("진작 대출되어 있는 책입니다.");
        }

        /*
        // ※ 책 이름으로 히스토리 저장하는 경우
        if (userLoanHistoryRepository.existsByBookNameAndIsReturn(book.getName(), false)) {
            throw new IllegalArgumentException("진작 대출되어 있는 책입니다.");
        }
        */

        // 4. 유저 정보를 가져온다
        User user = userRepository.findByName(request.getUserName())
                .orElseThrow(IllegalArgumentException::new);
        
        // 5. 아래 코드 대체
        user.loanBook(book);

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
        // 1. 유저이름으로 유저정보를 가져온다 (select * from user where user.name = ?)
        User user = userRepository.findByName(request.getUserName())
                .orElseThrow(IllegalArgumentException::new);

        // 2. 지연 로딩(Lazy Loading): 해당 유저의 UserLoanHistory 정보를 가져온다.
        Book book = bookRepository.findByName(request.getBookName())
                .orElseThrow(IllegalArgumentException::new);

        user.returnBook(book);

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
