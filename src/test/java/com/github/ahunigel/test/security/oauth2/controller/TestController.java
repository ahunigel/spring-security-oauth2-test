package com.github.ahunigel.test.security.oauth2.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = {"/api/test"}, produces = MediaType.APPLICATION_JSON_VALUE)
public class TestController {
  @Autowired
  private TestService service;
  @GetMapping
  public List<TestDTO> getAllTest(){
    return service.getAllTest();
  }
}
