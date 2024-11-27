package com.github.ahunigel.test.security.oauth2;

import com.github.ahunigel.test.security.AttachClaims;
import com.github.ahunigel.test.security.util.ClaimUtils;
import com.github.ahunigel.test.security.util.MockUserUtils;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithSecurityContext;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Created by Nigel Zheng on 8/3/2018.
 * <p>
 * Emulate running with a mocked oauth2 client on behalf of user,
 * attach claims as map to mocked oauth2 authentication details
 */
//@WithToken
@Retention(RetentionPolicy.RUNTIME)
@WithSecurityContext(factory = WithMockOAuth2User.WithMockOAuth2UserSecurityContextFactory.class)
public @interface WithMockOAuth2User {
  WithMockOAuth2Client client() default @WithMockOAuth2Client();

  WithMockUser user() default @WithMockUser();

  /**
   * Return the contained {@link AttachClaims} annotations.
   *
   * @return the claims
   */
  AttachClaims claims() default @AttachClaims({});

  class WithMockOAuth2UserSecurityContextFactory implements WithSecurityContextFactory<WithMockOAuth2User> {

    @Override
    public SecurityContext createSecurityContext(final WithMockOAuth2User annotation) {
      final SecurityContext ctx = SecurityContextHolder.createEmptyContext();
//      SecurityMockServerConfigurers.OAuth2LoginMutator  mutator = SecurityMockServerConfigurers.mockOAuth2Login();
      OAuth2AccessToken accessToken = new OAuth2AccessToken(OAuth2AccessToken.TokenType.BEARER, "access-token", null,
          null, Collections.singleton("read"));
      ClientRegistration.Builder c = ClientRegistration.withRegistrationId("test")
          .authorizationGrantType(AuthorizationGrantType.PASSWORD)
          .clientId(annotation.client().clientId())
          .tokenUri("https://token-uri.example.org");
      SimpleGrantedAuthority[] authorities = Arrays.stream(annotation.client().authorities()).map(SimpleGrantedAuthority::new)
          .toArray(SimpleGrantedAuthority[]::new);
      OAuth2Authentication oAuth2Authentication = new OAuth2Authentication(
          WithMockOAuth2Client.WithMockOAuth2ClientSecurityContextFactory.getOAuth2Request(annotation.client()),
          MockUserUtils.getAuthentication(annotation.user()));
      Map<String, Object> claims = ClaimUtils.extractClaims(annotation.claims());
      if (!claims.isEmpty()) {
        oAuth2Authentication.setDetails(claims);
      }
      OAuth2LoginMutator mutator = new OAuth2LoginMutator(accessToken).clientRegistration(c.build()).authorities(authorities).attributes(claims::get);
      OAuth2AuthenticationToken oAuth2AuthenticationToken = mutator.getToken();
      ctx.setAuthentication(oAuth2AuthenticationToken);
//      ctx.setAuthentication(oAuth2Authentication);
      SecurityContextHolder.setContext(ctx);
      return ctx;
    }

  }
}
