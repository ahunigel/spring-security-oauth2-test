package com.github.ahunigel.test.security;

import com.github.ahunigel.test.security.util.MockUserUtils;
import org.springframework.core.annotation.AliasFor;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithSecurityContext;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

import java.lang.annotation.*;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by Nigel Zheng on 8/3/2018.
 * <p>
 * Emulate running with a mocked user,
 * attach claims as map to mocked authentication details
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
@WithSecurityContext(factory = WithMockUserAndClaims.WithMockUserWithClaimsSecurityContextFactory.class)
public @interface WithMockUserAndClaims {
  /**
   * Return the contained {@link Claim} annotations.
   *
   * @return the claims
   */
  @AliasFor("claims")
  Claim[] value();

  @AliasFor("value")
  Claim[] claims();

  WithMockUser user() default @WithMockUser();

  class WithMockUserWithClaimsSecurityContextFactory implements WithSecurityContextFactory<WithMockUserAndClaims> {
    @Override
    public SecurityContext createSecurityContext(WithMockUserAndClaims annotation) {
      SecurityContext context = MockUserUtils.getSecurityContext(annotation.user());
      Authentication authentication = context.getAuthentication();
      Map<String, String> claims = Arrays.stream(annotation.value()).collect(Collectors.toMap(Claim::name, Claim::value));
      if (!claims.isEmpty() && authentication instanceof AbstractAuthenticationToken) {
        ((AbstractAuthenticationToken) authentication).setDetails(claims);
      }
      return context;
    }

  }

}
