package com.github.ahunigel.test.security.oauth2;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by Nigel Zheng on 8/8/2018.
 * <p>
 * With this annotation, non-token-based authentication is allowed on these resources.
 * Then an incoming cookie can populate the security context and
 * allow access to a caller that isn't an OAuth2 client.
 *
 * @author nigel
 */
@Import({ResourcesNonStateless.NonStatelessResourceServerConfig.class})
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ResourcesNonStateless {
  @TestConfiguration
  class NonStatelessResourceServerConfig extends ResourceServerConfigurerAdapter {

    @Override
    public void configure(ResourceServerSecurityConfigurer resources) throws Exception {
      resources.stateless(false);
    }

    @Override
    public void configure(HttpSecurity http) throws Exception {
    }
  }
}
