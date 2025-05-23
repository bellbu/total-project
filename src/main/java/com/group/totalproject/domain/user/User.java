package com.group.totalproject.domain.user;

import com.group.totalproject.domain.book.Book;
import com.group.totalproject.domain.user.loanhistory.UserLoanHistory;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity // @Entity: 스프링의 객체와 DB의 테이블을 매핑
@Table(indexes = { @Index(name = "idx_user_name", columnList = "name") })
@NoArgsConstructor(access = AccessLevel.PROTECTED) // User의 기본 생성자를 자동으로 생성
public class User {

    @Id // 해당 필드를 pk로 간주
    @GeneratedValue(strategy = GenerationType.IDENTITY) // pk 자동 생성: strategy = GenerationType.IDENTITY(MySQL의 auto_increment와 동일)
    private Long id = null;

    @Column(nullable = false, length = 20) // name varchar(20)
    private String name;

    private Integer age; // age는 null이 올 수 있고, Integer는 DB의 int와 동일하므로 @Column 생략 가능

    // @OneToMany: 유저 입장: 1(User[부모]) : N(UserLoanHistory[자식])
    // mappedBy: 양방향 매핑에서 주인이 아닌쪽에 설정 - User 엔티티에서 userLoanHistories 필드는 관계의 비주인, UserLoanHistory 엔티티의 user 필드가 관계의 주인
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserLoanHistory> userLoanHistories = new ArrayList<>();

    // protected User() {}  // JPA 사용하기 위해선 기본 생성자 필요
    public User(String name, Integer age) {
        if (name == null || name.isBlank()) {                                                       // IllegalArgumentException: 메서드에 잘못된 인수가 전달되었을 때 발생하는 예외
            throw new IllegalArgumentException(String.format("잘못된 name(%s)이 들어왔습니다.", name)); // String.format(): 리턴되는 문자열의 형태를 지정하는 메소드
        }
        this.name = name;
        this.age = age;
    }

    public String getName() {
        return name;
    }

    public Integer getAge() {
        return age;
    }

    public Long getId() {
        return id;
    }

    public void updateName(String name) {
        this.name = name;
    }

    public void loanBook(Book book) { // 도메인 계층에 비즈니스 로직 작성함
        this.userLoanHistories.add(new UserLoanHistory(this, book));
    }

    /*
    // ※ 책 이름으로 히스토리 저장하는 경우
    public void loanBook(String bookName) { // 도메인 계층에 비즈니스 로직 작성함
        this.userLoanHistories.add(new UserLoanHistory(this, bookName));
    }
    */

    /*
    public void returnBook(Book book) {
        UserLoanHistory targetHistory = this.userLoanHistories.stream() // .stream(): 리스트를 스트림으로 변환 => "select * from user_loan_history where user_id = ?" 와 동일
                .filter(history -> history.getBook().equals(book) && !history.isReturn())
                .findFirst() // .findFirst(): 필터링된 결과 중 첫 번째 값을 가져옴
                .orElseThrow(IllegalArgumentException::new); // .orElseThrow(): 책을 찾지 못한 경우 예외처리
        targetHistory.doReturn();
    }
    */

}
