package com.group.totalproject.domain.user.loanhistory;

import com.group.totalproject.domain.book.Book;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserLoanHistoryRepository extends JpaRepository<UserLoanHistory, Long> {
    // select * from user_loan_history where book_name = ? and is_return = ?;
    boolean existsByBookIdAndIsReturn(Long bookId, boolean isReturn);

    // 회원 삭제 시 모든 대출 중인 기록 조회
    boolean existsByUserIdAndIsReturnFalse(Long userId);

    // 책 반납 시 해당 책 대출 중인 기록 조회
    Optional<UserLoanHistory> findByUserIdAndBookIdAndIsReturnFalse(Long userId, Long bookId);

    /*
    // ※ 책 이름으로 히스토리 저장하는 경우
    boolean existsByBookNameAndIsReturn(String name, boolean isReturn);
    */

    /* // User 엔티티에서 서비스 로직처리 하므로 아래코드 삭제처리
    Optional<UserLoanHistory> findByUserIdAndBookName(long userId, String bookName);
    */

}

