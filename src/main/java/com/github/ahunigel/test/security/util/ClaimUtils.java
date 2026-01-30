package com.github.ahunigel.test.security.util;

import com.github.ahunigel.test.security.AttachClaims;
import com.github.ahunigel.test.security.Claim;
import org.apache.commons.beanutils.ConvertUtils;

import java.util.Arrays;
import java.util.HashMap;
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

  public static Map<String, Object> extractClaims(AttachClaims annotation) {
    Map<String, Object> claims = extractClaims(annotation.value());
    Map<String, String> pairs = extractClaims(annotation.claims());
    pairs.forEach(claims::putIfAbsent);
    return claims;
  }

  public static Map<String, Object> extractClaims(Claim[] claims) {
    return Arrays.stream(claims)
        .collect(Collectors.toMap(Claim::name, claim ->
            ConvertUtils.convert(claim.value(), claim.type())
        ));
  }

  public static Map<String, String> extractClaims(String... pairs) {
    final Map<String, String> map = new HashMap<>();
    for (String pair : pairs) {
      String separator = ":";
      if (!pair.contains(separator) && pair.contains("=")) {
        separator = "=";
      }
      int index = pair.indexOf(separator);
      String key = pair.substring(0, index > 0 ? index : pair.length());
      String value = index > 0 ? pair.substring(index + 1) : null;
      map.put(key, value);
    }
    return map;
  }
}
