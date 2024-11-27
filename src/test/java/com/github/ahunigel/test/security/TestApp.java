package com.github.ahunigel.test.security;

import lombok.SneakyThrows;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.SpringBootConfiguration;

/**
 * Created by nigel on 2020/3/22.
 *
 * @author nigel
 */
@SpringBootConfiguration
public class TestApp {
  @SneakyThrows
  public static void main(String[] args) {
    SpringApplication.run(TestApp.class, args);
  }

}
