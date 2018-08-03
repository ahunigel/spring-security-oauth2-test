package com.github.ahunigel.test.security;

import com.github.ahunigel.test.security.util.ClaimUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.test.context.TestSecurityContextHolder;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.support.AbstractTestExecutionListener;

import java.lang.reflect.AnnotatedElement;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Nigel.Zheng on 8/3/2018.
 */
public class AttachClaimsTestExecutionListener extends AbstractTestExecutionListener {
  private static final Logger logger = LoggerFactory.getLogger(AttachClaimsTestExecutionListener.class);

  @Override
  public void beforeTestClass(TestContext testContext) throws Exception {
    AttachClaims annotation = findAnnotation(testContext.getTestClass());

    if (annotation != null) {
      attachClaimsToAuthentication(annotation);
    }
  }

  @Override
  public void beforeTestMethod(TestContext testContext) throws Exception {
    AttachClaims annotation = findAnnotation(testContext.getTestMethod());

    if (annotation != null) {
      attachClaimsToAuthentication(annotation);
    }
  }

  private AttachClaims findAnnotation(AnnotatedElement annotated) {
    return AnnotationUtils.findAnnotation(annotated, AttachClaims.class);
  }

  public void attachClaimsToAuthentication(AttachClaims annotation) {
    Authentication authentication = TestSecurityContextHolder.getContext().getAuthentication();
    if (authentication != null && authentication instanceof AbstractAuthenticationToken) {
      Map<String, Object> claims = ClaimUtils.getClaims(annotation.value());
      Map<String, String> stringMap = toMap(annotation.claims());
      stringMap.entrySet().stream().forEach(entry -> claims.putIfAbsent(entry.getKey(), entry.getValue()));
      if (!claims.isEmpty()) {
        ((AbstractAuthenticationToken) authentication).setDetails(claims);
      }
    } else {
      logger.warn("No authentication found, do not attach claims");
    }
  }

  private Map<String, String> toMap(String[] claims) {
    final Map<String, String> map = new HashMap<>();
    for (int i = 0; i + 1 < claims.length; i += 2) {
      map.put(claims[i], claims[i + 1]);
    }
    return map;
  }
}
