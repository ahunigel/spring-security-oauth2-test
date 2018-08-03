package com.github.ahunigel.test.security;

import org.springframework.beans.BeanUtils;
import org.springframework.core.annotation.AnnotationUtils;
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
   * @return the claim
   */
  Claim[] value();

  WithMockUser user() default @WithMockUser();

  class WithClaimsSecurityContextFactory implements WithSecurityContextFactory<WithClaims> {
    @Override
    public SecurityContext createSecurityContext(WithClaims annotation) {
      WithSecurityContext withSecurityContext = AnnotationUtils
          .findAnnotation(annotation.user().getClass(), WithSecurityContext.class);
      WithSecurityContextFactory factory = createFactory(withSecurityContext);
      SecurityContext context = factory.createSecurityContext(annotation.user());
      Authentication authentication = context.getAuthentication();
      Map<String, String> claims = Arrays.stream(annotation.value()).collect(Collectors.toMap(Claim::name, Claim::value));
      ((AbstractAuthenticationToken) authentication).setDetails(claims);
      return context;
    }

    private WithSecurityContextFactory<? extends Annotation> createFactory(
        WithSecurityContext withSecurityContext) {
      Class<? extends WithSecurityContextFactory<? extends Annotation>> clazz = withSecurityContext
          .factory();
      try {
        return BeanUtils.instantiateClass(clazz);
      } catch (IllegalStateException e) {
        return BeanUtils.instantiateClass(clazz);
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    }
  }

}
