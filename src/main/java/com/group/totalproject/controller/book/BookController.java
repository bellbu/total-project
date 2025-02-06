package com.group.totalproject.controller.book;

import com.group.totalproject.dto.book.request.BookCreateRequest;
import com.group.totalproject.dto.book.request.BookLoanRequest;
import com.group.totalproject.dto.book.request.BookReturnRequest;
import com.group.totalproject.service.book.BookService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BookController {

    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @PostMapping("/book")
    public ResponseEntity<?> saveBook(@RequestBody BookCreateRequest request) {
        try {
            bookService.saveBook(request);
            return ResponseEntity.ok("책 등록이 완료되었습니다.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/book/loan")
    public ResponseEntity<?> loanBook(@RequestBody BookLoanRequest request) {
        try {
            bookService.loanBook(request);
            return ResponseEntity.ok("책 대출이 완료되었습니다.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/book/return")
    public ResponseEntity<?> returnBook(@RequestBody BookReturnRequest request) {
        try {
            bookService.returnBook(request);
            return ResponseEntity.ok("책 반납이 완료되었습니다.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

}
