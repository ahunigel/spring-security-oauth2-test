package com.github.ahunigel.test.security;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContext;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

import java.lang.annotation.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by Nigel Zheng on 8/3/2018.
 * <p>
 * Attach claims as map to current authentication details
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
@WithSecurityContext(factory = WithClaims.WithClaimsSecurityContextFactory.class)
public @interface WithClaims {
  /**
   * Return the contained {@link Claim} annotations.
   *
   * @return the claims
   */
  Claim[] value();

  /**
   * key-value paired string array, redundant value will be ignored
   * <p>
   * would merge with #value() if key is absent
   *
   * @return
   */
  String[] claims() default {};

  class WithClaimsSecurityContextFactory implements WithSecurityContextFactory<WithClaims> {
    @Override
    public SecurityContext createSecurityContext(WithClaims annotation) {
      SecurityContext context = SecurityContextHolder.getContext();
      Authentication authentication = context.getAuthentication();
      if (authentication != null && authentication instanceof AbstractAuthenticationToken) {
        Map<String, String> claims = Arrays.stream(annotation.value()).collect(Collectors.toMap(Claim::name, Claim::value));
        Map<String, String> stringMap = toMap(annotation.claims());
        stringMap.entrySet().stream().forEach(entry -> claims.putIfAbsent(entry.getKey(), entry.getValue()));
        if (!claims.isEmpty()) {
          ((AbstractAuthenticationToken) authentication).setDetails(claims);
        }
      }
      return context;
    }

    private Map<String, String> toMap(String[] claims) {
      final Map<String, String> map = new HashMap<>();
      for (int i = 0; i + 1 < claims.length; i += 2) {
        map.put(claims[i], claims[i + 1]);
      }
      return map;
    }
  }

}
