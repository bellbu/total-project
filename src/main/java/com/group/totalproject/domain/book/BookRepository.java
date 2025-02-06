package com.group.totalproject.domain.book;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface BookRepository extends JpaRepository<Book, Long> {
    
    // 책 이름으로 Book 객체 조회
    Optional<Book> findByName(String name);

    // 책 이름으로 bookId 조회
    @Query("SELECT b.id FROM Book b WHERE b.name = :name")
    Optional<Long> findIdByName(@Param("name") String name);

    // 동일 책 이름 존재 여부 확인
    boolean existsByName(String name);


}
