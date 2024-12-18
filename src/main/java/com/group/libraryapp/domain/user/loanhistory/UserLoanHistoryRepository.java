package com.group.libraryapp.domain.user.loanhistory;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserLoanHistoryRepository extends JpaRepository<UserLoanHistory, Long> {
    // select * from user_loan_history where book_name = ? and is_return = ?;
    boolean existsByBookNameAndIsReturn(String name, boolean isReturn);

    /* // User 엔티티에서 서비스 로직처리 하므로 아래코드 삭제처리
    Optional<UserLoanHistory> findByUserIdAndBookName(long userId, String bookName);
    */
}

