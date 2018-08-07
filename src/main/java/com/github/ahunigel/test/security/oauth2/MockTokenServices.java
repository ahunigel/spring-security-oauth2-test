package com.github.ahunigel.test.security.oauth2;

import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.MockReset;
import org.springframework.security.oauth2.provider.token.ResourceServerTokenServices;

/**
 * Created by Nigel Zheng on 8/7/2018.
 */
@MockBean(value = {ResourceServerTokenServices.class}, reset = MockReset.NONE)
public @interface MockTokenServices {
}
