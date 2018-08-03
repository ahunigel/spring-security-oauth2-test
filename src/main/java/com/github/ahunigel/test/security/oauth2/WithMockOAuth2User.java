package com.github.ahunigel.test.security.oauth2;

import com.github.ahunigel.test.security.Claim;
import com.github.ahunigel.test.security.util.ClaimUtils;
import com.github.ahunigel.test.security.util.MockUserUtils;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithSecurityContext;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Map;

/**
 * Created by Nigel Zheng on 8/3/2018.
 * <p>
 * Emulate running with a mocked oauth2 client on behalf of user,
 * attach claims as map to mocked oauth2 authentication details
 */
@Retention(RetentionPolicy.RUNTIME)
@WithSecurityContext(factory = WithMockOAuth2User.WithMockOAuth2UserSecurityContextFactory.class)
public @interface WithMockOAuth2User {
  WithMockOAuth2Client client() default @WithMockOAuth2Client();

  WithMockUser user() default @WithMockUser();

  /**
   * Return the contained {@link Claim} annotations.
   *
   * @return the claims
   */
  Claim[] claims();

  class WithMockOAuth2UserSecurityContextFactory implements WithSecurityContextFactory<WithMockOAuth2User> {

    @Override
    public SecurityContext createSecurityContext(final WithMockOAuth2User annotation) {
      final SecurityContext ctx = SecurityContextHolder.createEmptyContext();
      OAuth2Authentication oAuth2Authentication = new OAuth2Authentication(
          WithMockOAuth2Client.WithMockOAuth2ClientSecurityContextFactory.getOAuth2Request(annotation.client()),
          MockUserUtils.getAuthentication(annotation.user()));
      Map<String, Object> claims = ClaimUtils.getClaims(annotation.claims());
      if (!claims.isEmpty()) {
        oAuth2Authentication.setDetails(claims);
      }
      ctx.setAuthentication(oAuth2Authentication);
      SecurityContextHolder.setContext(ctx);
      return ctx;
    }

  }
}
