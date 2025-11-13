package com.likelion.nextworld;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class NextworldApplication {

  public static void main(String[] args) {
    SpringApplication.run(NextworldApplication.class, args);
  }
}
