package com.github.ahunigel.test.security.helper;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

/**
 * Created by Nigel Zheng on 7/31/2018.
 */
@TestConfiguration
public class OAuth2MockMvcConfig {
  @Bean
  public SerializationHelper serializationHelper(List<HttpMessageConverter<?>> messageConverters) {
    return new SerializationHelper(messageConverters);
  }

  @Bean
  public OAuth2MockMvcHelper mockMvcHelper(
      MockMvc mockMvc,
      List<HttpMessageConverter<?>> messageConverters,
      @Value("${controllers.default-media-type:application/json;charset=UTF-8}") MediaType defaultMediaType) {
    return new OAuth2MockMvcHelper(mockMvc, messageConverters, defaultMediaType);
  }
}
