package com.group.totalproject.domain.user.loanhistory;

import com.group.totalproject.domain.book.Book;
import com.group.totalproject.domain.user.User;
import jakarta.persistence.*;

import java.time.LocalDateTime;


@Entity
public class UserLoanHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id = null;

    // @JoinColumn(조인 대상 컬럼): 디폴트로 user_id(엔티티의 필드명+_+엔티티 PK)으로 테이블에 저장됨
    // @JoinColumn(name = [], referencedColumnName = []): name - 컬럼 이름 변경 가능, referencedColumnName - 조인 대상 컬럼 지정 가능
    @JoinColumn(nullable = false)
    @ManyToOne // 히스토리 입장: N(히스토리) : 1(유저) 관계
    private User user;

    @JoinColumn(nullable = false)
    @ManyToOne // 히스토리 입장: N(히스토리) : 1(유저) 관계
    private Book book;

    /*
    // ※ 책 이름으로 히스토리 저장하는 경우
    private String bookName;
    */

    private boolean isReturn; // DB에 1(true)인 경우 반납한 경우, 0(false)인 경우 반납되지 않은 경우

    protected UserLoanHistory() {
    }

    public UserLoanHistory(User user, Book book) {
        this.user = user;
        this.book = book;
        this.isReturn = false;
    }

    /*
    // ※ 책 이름으로 히스토리 저장하는 경우
    public UserLoanHistory(User user, String bookName) {
        this.user = user;
        this.bookName = bookName;
        this.isReturn = false;
    }*/

    public void doReturn() {
        this.isReturn = true;
    }

    public boolean isReturn() {
        return this.isReturn;
    }

    public Book getBook() {
        return this.book;
    }

    /*
    // ※ 책 이름으로 히스토리 저장하는 경우
    public String getBookName() {
        return this.bookName;
    }*/

}
