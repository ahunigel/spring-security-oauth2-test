package com.github.ahunigel.test.security.oauth2;

import com.github.ahunigel.test.security.util.ClaimUtils;
import com.github.ahunigel.test.security.util.MockUserUtils;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.OAuth2Request;
import org.springframework.security.test.context.support.WithSecurityContext;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by Nigel Zheng on 8/3/2018.
 * <p>
 * Emulate running with a mocked oauth2 client
 */
//@WithToken
@Retention(RetentionPolicy.RUNTIME)
@WithSecurityContext(factory = WithMockOAuth2Client.WithMockOAuth2ClientSecurityContextFactory.class)
public @interface WithMockOAuth2Client {
  String clientId() default "web-client";

  String[] scope() default {"openid"};

  String[] authorities() default {};

  boolean approved() default true;

  class WithMockOAuth2ClientSecurityContextFactory implements WithSecurityContextFactory<WithMockOAuth2Client> {

    public static OAuth2Request getOAuth2Request(final WithMockOAuth2Client annotation) {
      final Set<? extends GrantedAuthority> authorities = Stream.of(annotation.authorities())
          .map(SimpleGrantedAuthority::new)
          .collect(Collectors.toSet());

      final Set<String> scope = Stream.of(annotation.scope())
          .collect(Collectors.toSet());

      return new OAuth2Request(
          null,
          annotation.clientId(),
          authorities,
          annotation.approved(),
          scope,
          null,
          null,
          null,
          null);
    }

    @Override
    public SecurityContext createSecurityContext(final WithMockOAuth2Client annotation) {
      final SecurityContext ctx = SecurityContextHolder.createEmptyContext();
//      ctx.setAuthentication(new OAuth2Authentication(getOAuth2Request(annotation), null));

      OAuth2AccessToken accessToken = new OAuth2AccessToken(OAuth2AccessToken.TokenType.BEARER, "access-token", null,
          null, Collections.singleton("read"));
      ClientRegistration.Builder c = ClientRegistration.withRegistrationId("test")
          .authorizationGrantType(AuthorizationGrantType.PASSWORD)
          .clientId(annotation.clientId())
          .tokenUri("https://token-uri.example.org");
      SimpleGrantedAuthority[] authorities = Arrays.stream(annotation.authorities()).map(SimpleGrantedAuthority::new)
          .toArray(SimpleGrantedAuthority[]::new);
      OAuth2LoginMutator mutator = new OAuth2LoginMutator(accessToken).clientRegistration(c.build()).authorities(authorities);
      OAuth2AuthenticationToken oAuth2AuthenticationToken = mutator.getToken();
      ctx.setAuthentication(oAuth2AuthenticationToken);
      SecurityContextHolder.setContext(ctx);
      return ctx;
    }
  }
}
