package com.github.ahunigel.test.security.oauth2;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.OAuth2Request;
import org.springframework.security.test.context.support.WithSecurityContext;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by Nigel Zheng on 8/3/2018.
 * <p>
 * Emulate running with a mocked oauth2 client
 */
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
          .map(auth -> new SimpleGrantedAuthority(auth))
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
      ctx.setAuthentication(new OAuth2Authentication(getOAuth2Request(annotation), null));
      SecurityContextHolder.setContext(ctx);
      return ctx;
    }
  }
}
