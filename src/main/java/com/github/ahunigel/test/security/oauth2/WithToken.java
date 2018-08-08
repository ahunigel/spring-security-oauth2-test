package com.github.ahunigel.test.security.oauth2;

import org.springframework.security.oauth2.provider.token.ResourceServerTokenServices;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by Nigel Zheng on 8/7/2018.
 * <p>
 * Emulate an OAuth2 token request, would extract an {@link PreAuthenticatedAuthenticationToken}
 * <p>
 * require annotation {@link MockTokenServices} on test class level or {@link ResourceServerTokenServices} @MockBean exists
 *
 * @author nigel
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface WithToken {

  String FAKE_BEARER_TOKEN = "fake.bearer.token";

  String value() default FAKE_BEARER_TOKEN;
}
