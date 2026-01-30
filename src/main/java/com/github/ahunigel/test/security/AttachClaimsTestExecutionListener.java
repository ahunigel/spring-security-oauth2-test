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
import java.util.Map;

/**
 * Created by Nigel Zheng on 8/3/2018.
 *
 * @author nigel
 */
public class AttachClaimsTestExecutionListener extends AbstractTestExecutionListener {
  private static final Logger log = LoggerFactory.getLogger(AttachClaimsTestExecutionListener.class);

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
    if (authentication instanceof AbstractAuthenticationToken authToken) {
      Map<String, Object> claims = ClaimUtils.extractClaims(annotation);
      if (!claims.isEmpty()) {
        authToken.setDetails(claims);
      }
    } else {
      log.warn("No authentication found, do not attach claims");
    }
  }

}
