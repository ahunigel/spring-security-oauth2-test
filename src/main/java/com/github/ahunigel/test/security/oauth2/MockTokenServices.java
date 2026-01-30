package com.github.ahunigel.test.security.oauth2;

import org.springframework.security.oauth2.provider.token.ResourceServerTokenServices;
import org.springframework.test.context.bean.override.mockito.MockReset;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by Nigel Zheng on 8/7/2018.
 */
@Retention(RetentionPolicy.RUNTIME)
@MockitoBean(types = {ResourceServerTokenServices.class}, reset = MockReset.AFTER)
public @interface MockTokenServices {
}
