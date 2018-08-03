package com.github.ahunigel.test.security;

import java.lang.annotation.*;

/**
 * Created by Nigel Zheng on 8/3/2018.
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Repeatable(WithClaims.class)
public @interface Claim {

  String name();

  String value();
}
