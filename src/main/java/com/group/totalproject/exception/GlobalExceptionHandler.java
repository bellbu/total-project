package com.group.totalproject.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ResponseStatusException;

@ControllerAdvice // @ControllerAdvice: 전역 예외 처리를 담당
public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class) // @ExceptionHandler: 특정 예외를 처리하는 메서드 정의(IllegalArgumentException 예외가 발생했을 때 이 메서드 실행)
    public ResponseEntity<String> handleIllegalArgumentException(IllegalArgumentException e) { // ResponseEntity: HTTP 응답 객체(상태 코드, 헤더, 바디)
        // HTTP 상태 코드 400과 함께 예외 메시지를 반환
        return ResponseEntity.badRequest().body(e.getMessage());
    }

}