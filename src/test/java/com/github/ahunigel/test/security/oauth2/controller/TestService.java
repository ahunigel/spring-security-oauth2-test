package com.github.ahunigel.test.security.oauth2.controller;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.LongStream;

@Service
public class TestService {

  public List<TestDTO> getAllTest() {
    return LongStream.range(0, 10).mapToObj(i -> {
      TestDTO dto = new TestDTO();
      dto.setId(i);
      dto.setName("name" + i);
      return dto;
    }).toList();
  }
}
