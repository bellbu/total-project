package com.group.totalproject;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication // 스프링 실행 시 설정들을 자동으로 해줌
public class TotalProjectApplication {

  public static void main(String[] args) {
    SpringApplication.run(TotalProjectApplication.class, args); // 서버 실행
  }

}
