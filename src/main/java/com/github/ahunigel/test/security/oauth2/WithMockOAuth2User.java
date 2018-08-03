package com.github.ahunigel.test.security.oauth2;

import com.github.ahunigel.test.security.Claim;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithSecurityContext;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by Nigel Zheng on 8/3/2018.
 */
@Retention(RetentionPolicy.RUNTIME)
@WithSecurityContext(factory = WithMockOAuth2User.WithMockOAuth2UserSecurityContextFactory.class)
public @interface WithMockOAuth2User {
  WithMockOAuth2Client client() default @WithMockOAuth2Client();

  WithMockUser user() default @WithMockUser();

  Claim[] claims();

  class WithMockOAuth2UserSecurityContextFactory implements WithSecurityContextFactory<WithMockOAuth2User> {

    /**
     * Sadly, #WithMockUserSecurityContextFactory is not public,
     * so re-implement mock user authentication creation
     *
     * @param user
     * @return an Authentication with provided user details
     */
    public static UsernamePasswordAuthenticationToken getUserAuthentication(final WithMockUser user) {
      final String principal = user.username().isEmpty() ? user.value() : user.username();

      final Stream<String> grants = user.authorities().length == 0 ?
          Stream.of(user.roles()).map(r -> "ROLE_" + r) :
          Stream.of(user.authorities());

      final Set<? extends GrantedAuthority> userAuthorities = grants
          .map(auth -> new SimpleGrantedAuthority(auth))
          .collect(Collectors.toSet());

      return new UsernamePasswordAuthenticationToken(
          new User(principal, user.password(), userAuthorities),
          principal + ":" + user.password(),
          userAuthorities);
    }

    @Override
    public SecurityContext createSecurityContext(final WithMockOAuth2User annotation) {
      final SecurityContext ctx = SecurityContextHolder.createEmptyContext();
      OAuth2Authentication oAuth2Authentication = new OAuth2Authentication(
          WithMockOAuth2Client.WithMockOAuth2ClientSecurityContextFactory.getOAuth2Request(annotation.client()),
          getUserAuthentication(annotation.user()));
      Map<String, String> claims = Arrays.stream(annotation.claims()).collect(Collectors.toMap(Claim::name, Claim::value));
      if (!claims.isEmpty()) {
        oAuth2Authentication.setDetails(claims);
      }
      ctx.setAuthentication(oAuth2Authentication);
      SecurityContextHolder.setContext(ctx);
      return ctx;
    }

    private Map<String, Object> toMap(String[] claims) {
      final Map<String, Object> map = new HashMap<>();
      for (int i = 0; i + 1 < claims.length; i += 2) {
        map.put(claims[i], claims[i + 1]);
      }
      return map;
    }
  }
}
