package com.group.totalproject.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice // @ControllerAdvice: 전역 예외 처리를 담당
public class GlobalExceptionHandler {

    @ExceptionHandler(RuntimeException.class) // @ExceptionHandler: 특정 예외를 처리하는 메서드 정의(RuntimeException 예외가 발생했을 때 이 메서드 실행)
    public ResponseEntity<String> handleRuntimeException(RuntimeException e) { // ResponseEntity: HTTP 응답 객체(상태 코드, 헤더, 바디)
        // .badRequest(): HTTP 상태 코드 400(Bad Request)를 설정
        // .body(e.getMessage()): 예외 메시지(e.getMessage())를 응답 바디로 설정
        return ResponseEntity.badRequest().body(e.getMessage());
    }
}