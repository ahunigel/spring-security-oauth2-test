package com.github.ahunigel.test.security.oauth2;

import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.test.context.bean.override.mockito.MockReset;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Mock client registration repository
 * <p>
 * Created by ahunigel on 10/16/2024.
 */
@Retention(RetentionPolicy.RUNTIME)
@MockitoBean(types = {InMemoryClientRegistrationRepository.class}, reset = MockReset.AFTER)
public @interface MockClientRegistration {
}
