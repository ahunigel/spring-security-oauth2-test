package com.github.ahunigel.test.security.oauth2;

import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetails;
import org.springframework.security.oauth2.provider.token.ResourceServerTokenServices;
import org.springframework.security.test.context.TestSecurityContextHolder;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.support.AbstractTestExecutionListener;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Map;

import static com.github.ahunigel.test.security.oauth2.TestSecurityConf.mockRepo;
import static org.mockito.Mockito.when;
import static org.springframework.security.oauth2.core.OAuth2AccessToken.TokenType.BEARER;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;

/**
 * Created by Nigel Zheng on 8/7/2018.
 * <p>
 * Add <code>Authorization</code> header to token request, extract an {@link PreAuthenticatedAuthenticationToken},
 * and then load an existing {@link OAuth2Authentication} from {@link SecurityContext}
 *
 * @author nigel
 */
public class WithTokenTestExecutionListener extends AbstractTestExecutionListener {
  private static final Logger log = LoggerFactory.getLogger(WithTokenTestExecutionListener.class);

  private static final String ORIGINAL_REQUEST_BUILDER = "originalRequestBuilder";

  @Override
  public void beforeTestClass(TestContext testContext) throws Exception {
    WithToken annotation = AnnotatedElementUtils.findMergedAnnotation(testContext.getTestClass(), WithToken.class);
    if (annotation != null) {
      verifyTokenServicesMocked(testContext.getTestClass());
      addAuthHeader(testContext.getTestClass(), testContext, annotation);
    }
  }

  @Override
  public void beforeTestMethod(TestContext testContext) throws Exception {
    WithToken annotation = AnnotatedElementUtils.findMergedAnnotation(testContext.getTestMethod(), WithToken.class);
    if (annotation != null) {
      verifyTokenServicesMocked(testContext.getTestClass());
      addAuthHeader(testContext.getTestMethod(), testContext, annotation);
    }
  }

  @Override
  public void afterTestMethod(TestContext testContext) throws Exception {
    Annotation annotation = AnnotatedElementUtils.findMergedAnnotation(testContext.getTestMethod(), WithToken.class);
    if (annotation != null) {
      removeAuthHeader(testContext.getTestMethod(), testContext);
    }
  }

  @Override
  public void afterTestClass(TestContext testContext) throws Exception {
    Annotation annotation = AnnotatedElementUtils.findMergedAnnotation(testContext.getTestClass(), WithToken.class);
    if (annotation != null) {
      removeAuthHeader(testContext.getTestClass(), testContext);
    }
  }

  private void verifyTokenServicesMocked(Class<?> testClass) {
    MockTokenServices annotation = AnnotatedElementUtils.findMergedAnnotation(testClass, MockTokenServices.class);
//    Assert.state(annotation != null, "Missing @MockTokenServices on class level");
  }

  private void addAuthHeader(AnnotatedElement annotated, TestContext testContext, WithToken withToken) {
    Assert.state(withToken != null, "No @WithToken exists!!!");
    MockMvc mockMvc = testContext.getApplicationContext().getBean(MockMvc.class);
    MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get("/")
        .with(testSecurityContext()).with(bearerTokenProcessor(withToken.value()))
            .with(oidcLogin())
            .with(oauth2Client("my-trusted-client"))
//                .accessToken(new OAuth2AccessToken(BEARER, "token", null, null, Collections.singleton("message:read"))))
        ;
    // stash original default request builder
    RequestBuilder originalRequestBuilder = (RequestBuilder) ReflectionTestUtils.getField(mockMvc, MockMvc.class,
        "defaultRequestBuilder");
    testContext.setAttribute(attributeName(annotated), originalRequestBuilder);
    setDefaultRequestBuilder(mockMvc, requestBuilder);

//    mockClientRegistrationRepo(testContext);

    mockTokenServices(testContext, withToken);
  }

  private void mockClientRegistrationRepo(TestContext testContext) {
    InMemoryClientRegistrationRepository clientRegistrationRepository = testContext.getApplicationContext().getBean(InMemoryClientRegistrationRepository.class);

    mockRepo(clientRegistrationRepository);
  }

  private void mockTokenServices(TestContext testContext, WithToken withToken) {
    ResourceServerTokenServices tokenServices = testContext.getApplicationContext().getBean(ResourceServerTokenServices.class);
    when(tokenServices.loadAuthentication(withToken.value())).thenAnswer(invocation -> {
      Authentication authentication = TestSecurityContextHolder.getContext().getAuthentication();
      if (authentication instanceof OAuth2Authentication) {
        if (authentication.getDetails() instanceof OAuth2AuthenticationDetails) {
          // reset details to claims when invoke mockmvc request more than once
          Object decodedDetails = ((OAuth2AuthenticationDetails) authentication.getDetails()).getDecodedDetails();
          if (decodedDetails instanceof Map) {
            ((OAuth2Authentication) authentication).setDetails(decodedDetails);
          }
        }
        return (OAuth2Authentication) authentication;
      }
      return null;
    });
  }

  private void removeAuthHeader(AnnotatedElement annotated, TestContext testContext) {
    MockMvc mockMvc = testContext.getApplicationContext().getBean(MockMvc.class);
    Object originalRequestBuilder = testContext.getAttribute(attributeName(annotated));
    if (originalRequestBuilder instanceof RequestBuilder) {
      // reset default request builder
      setDefaultRequestBuilder(mockMvc, (RequestBuilder) originalRequestBuilder);
    }
    Mockito.reset(new ResourceServerTokenServices[]{
        testContext.getApplicationContext().getBean(ResourceServerTokenServices.class)
    });
  }

  private String attributeName(AnnotatedElement annotated) {
    return ORIGINAL_REQUEST_BUILDER + annotated.getClass().getSimpleName()
        + (annotated instanceof Method ? ((Method) annotated).getName() : "");
  }

  private void setDefaultRequestBuilder(MockMvc mockMvc, RequestBuilder requestBuilder) {
    ReflectionTestUtils.invokeSetterMethod(mockMvc, "setDefaultRequest", requestBuilder, RequestBuilder.class);
  }

  private RequestPostProcessor bearerTokenProcessor(String token) {
    return mockRequest -> {
      String authHeader = mockRequest.getHeader("Authorization");
      if (!StringUtils.hasText(authHeader)) {
        mockRequest.addHeader("Authorization", "Bearer " + token);
      } else {
        log.warn("DO NOT OVERRIDE existing authorization header: {}", authHeader);
      }
      return mockRequest;
    };
  }
}
