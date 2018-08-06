package com.github.ahunigel.test.security.util;

import com.github.ahunigel.test.security.Claim;
import org.apache.commons.beanutils.ConvertUtils;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by Nigel Zheng on 8/3/2018.
 *
 * @author nigel
 */
public class ClaimUtils {
  private ClaimUtils() {
    throw new InstantiationError();
  }

  public static Map<String, Object> getClaims(Claim[] claims) {
    return Arrays.stream(claims)
        .collect(Collectors.toMap(Claim::name, claim ->
            ConvertUtils.convert(claim.value(), claim.type())
        ));
  }
}
