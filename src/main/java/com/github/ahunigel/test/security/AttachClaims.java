package com.github.ahunigel.test.security;

import java.lang.annotation.*;

/**
 * Created by Nigel Zheng on 8/3/2018.
 * <p>
 * Attach claims as map to current authentication details
 *
 * @author nigel
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface AttachClaims {
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

}
