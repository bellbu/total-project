package com.group.totalproject.controller.book;

import com.group.totalproject.dto.book.request.BookCreateRequest;
import com.group.totalproject.dto.book.request.BookLoanRequest;
import com.group.totalproject.dto.book.request.BookReturnRequest;
import com.group.totalproject.dto.book.response.LoanResponse;
import com.group.totalproject.service.book.BookService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
public class BookController {

    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @GetMapping("/book") // 목록보기
    public List<LoanResponse> getLoans() {
        return bookService.getLoans();
    }

    @PostMapping("/book")
    public ResponseEntity<?> saveBook(@RequestBody BookCreateRequest request) {
        try {
            bookService.saveBook(request);
            return ResponseEntity.ok("책 등록이 완료되었습니다.");
        } catch (IllegalArgumentException e) {
            log.warn("[도서 등록 실패] 이유: {}", e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/book/loan")
    public ResponseEntity<?> loanBook(@RequestBody BookLoanRequest request) {
        log.info("[도서 대출 요청] 책 제목: {}, 회원: {}", request.getBookName(), request.getUserName());
        try {
            bookService.loanBook(request);
            log.info("[도서 대출 성공] 책 제목: {}", request.getBookName());
            return ResponseEntity.ok("책 대출이 완료되었습니다.");
        } catch (IllegalArgumentException e) {
            log.warn("[도서 대출 실패] 이유: {}", e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/book/return")
    public ResponseEntity<?> returnBook(@RequestBody BookReturnRequest request) {
        log.info("[도서 반납 요청] 책 제목: {}, 회원: {}", request.getBookName(), request.getUserName());
        try {
            bookService.returnBook(request);
            log.info("[도서 반납 성공] 책 ID: {}", request.getBookName());
            return ResponseEntity.ok("책 반납이 완료되었습니다.");
        } catch (IllegalArgumentException e) {
            log.warn("[도서 반납 실패] 이유: {}", e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

}
