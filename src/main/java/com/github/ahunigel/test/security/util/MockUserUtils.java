package com.github.ahunigel.test.security.util;

import org.springframework.beans.BeanUtils;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithSecurityContext;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

import java.lang.annotation.Annotation;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by Nigel Zheng on 8/3/2018.
 *
 * @author nigel
 */
public class MockUserUtils {
  private MockUserUtils() {
    throw new InstantiationError();
  }

  public static SecurityContext getSecurityContext(WithMockUser withMockUser) {
    WithSecurityContext withSecurityContext = AnnotationUtils
        .findAnnotation(withMockUser.getClass(), WithSecurityContext.class);
    WithSecurityContextFactory factory = createFactory(withSecurityContext);
    return factory.createSecurityContext(withMockUser);
  }

  public static Authentication getAuthentication(WithMockUser withMockUser) {
    return getSecurityContext(withMockUser).getAuthentication();
  }

  private static WithSecurityContextFactory<? extends Annotation> createFactory(
      WithSecurityContext withSecurityContext) {
    Class<? extends WithSecurityContextFactory<? extends Annotation>> clazz = withSecurityContext
        .factory();
    try {
      return BeanUtils.instantiateClass(clazz);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * User authentication creation
   *
   * @param user
   * @return an Authentication with provided user details
   */
  public static UsernamePasswordAuthenticationToken createUserAuthentication(final WithMockUser user) {
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
}
