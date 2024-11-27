package com.github.ahunigel.test.security.oauth2;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.security.oauth2.core.AuthorizationGrantType;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by Nigel Zheng on 8/7/2018.
 */
@Import({MockClientRegistration2.TestSecurityConf.class})
@Retention(RetentionPolicy.RUNTIME)
//@MockBean(value = {InMemoryClientRegistrationRepository.class}, reset = MockReset.AFTER)
public @interface MockClientRegistration2 {
  @TestConfiguration
  class TestSecurityConf {
    @Bean
    InMemoryClientRegistrationRepository clientRegistrationRepository() {
      final var clientRegistrationRepository = mock(InMemoryClientRegistrationRepository.class);
      when(clientRegistrationRepository.iterator()).thenReturn(Collections.emptyIterator());
      when(clientRegistrationRepository.spliterator()).thenReturn(new ArrayList<ClientRegistration>().spliterator());
      when(clientRegistrationRepository.findByRegistrationId(anyString()))
          .thenAnswer(
              invocation -> ClientRegistration
                  .withRegistrationId(invocation.getArgument(0))
                  .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                  .clientId(invocation.getArgument(0))
                  .redirectUri("http://localhost:8080/oauth2/code/%s".formatted(invocation.getArgument(0).toString()))
                  .authorizationUri("https://localhost:8443/auth")
                  .tokenUri("https://localhost:8443/token")
                  .build());
      return clientRegistrationRepository;
    }
  }
}
