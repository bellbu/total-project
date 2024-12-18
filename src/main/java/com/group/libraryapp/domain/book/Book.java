package com.group.libraryapp.domain.book;

import jakarta.persistence.*;

@Entity
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // id 자동 생성
    private Long id = null; // 명시적으로 보여주기 위해서 null을 넣어줌

    @Column(nullable = false) // 기본값 생략: length = 255, name = "name"
    private String name;

    protected Book() {
    }

    public Book(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException(String.format("잘못된 name(%s)이 들어왔습니다", name));
        }
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
