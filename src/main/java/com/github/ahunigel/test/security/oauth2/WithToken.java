package com.github.ahunigel.test.security.oauth2;

import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.MockReset;
import org.springframework.security.oauth2.provider.token.ResourceServerTokenServices;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by Nigel Zheng on 2018/8/7.
 * <p>
 * Emulate an OAuth2 token request, would extract an {@link PreAuthenticatedAuthenticationToken}
 *
 * @author nigel
 */
@MockBean(value = {ResourceServerTokenServices.class}, reset = MockReset.NONE)
@Retention(RetentionPolicy.RUNTIME)
public @interface WithToken {

  String FAKE_BEARER_TOKEN = "fake.bearer.token";

  String value() default FAKE_BEARER_TOKEN;
}
