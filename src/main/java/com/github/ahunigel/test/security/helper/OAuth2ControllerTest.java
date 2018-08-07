package com.github.ahunigel.test.security.helper;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.provider.token.ResourceServerTokenServices;
import org.springframework.test.context.junit4.SpringRunner;

import static org.mockito.Mockito.when;

/**
 * Created by Nigel.Zheng on 7/31/2018.
 */
@RunWith(SpringRunner.class)
@Import(OAuth2MockMvcConfig.class)
public class OAuth2ControllerTest {
  @MockBean
  private ResourceServerTokenServices tokenService;

  @Autowired
  protected OAuth2MockMvcHelper api;

  @Autowired
  protected SerializationHelper conv;

  @Before
  public void setUpTokenService() {
    when(tokenService.loadAuthentication(api.VALID_TEST_TOKEN_VALUE))
        .thenAnswer(invocation -> SecurityContextHolder.getContext().getAuthentication());
  }
}
