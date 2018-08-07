package com.github.ahunigel.test.security.helper;

import org.springframework.boot.test.context.TestComponent;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.oauth2.client.OAuth2ClientContext;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.OAuth2Request;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by Nigel Zheng on 8/2/2018.
 */
@TestComponent
public class OAuth2TestHelper {

  public OAuth2ClientContext getOAuth2ClientContext() {
    OAuth2ClientContext mockClient = mock(OAuth2ClientContext.class);
    when(mockClient.getAccessToken()).thenReturn(new DefaultOAuth2AccessToken("my-fun-token"));

    return mockClient;
  }

  public Authentication getOAuthTestAuthentication() {
    return getOAuthTestAuthentication(Collections.emptyMap());
  }

  public Authentication getOAuthTestAuthentication(Map<String, Object> claims) {
    OAuth2Authentication oauth = new OAuth2Authentication(getOAuth2Request(), getAuthentication(claims));
    if (oauth.getDetails() == null) {
      oauth.setDetails(oauth.getUserAuthentication().getDetails());
    }
    return oauth;
  }

  private OAuth2Request getOAuth2Request() {
    String clientId = "oauth-client-id";
    Map<String, String> requestParameters = Collections.emptyMap();
    boolean approved = true;
    String redirectUrl = "http://my-redirect-url.com";
    Set<String> responseTypes = Collections.emptySet();
    Set<String> scopes = Collections.emptySet();
    Set<String> resourceIds = Collections.emptySet();
    Map<String, Serializable> extensionProperties = Collections.emptyMap();
    List<GrantedAuthority> authorities = AuthorityUtils.createAuthorityList("Everything");

    return new OAuth2Request(requestParameters, clientId, authorities,
        approved, scopes, resourceIds, redirectUrl, responseTypes, extensionProperties);
  }

  private Authentication getAuthentication(Map<String, Object> claims) {
    List<GrantedAuthority> authorities = AuthorityUtils.createAuthorityList("Everything");

    User userPrincipal = new User("admin", "", true, true, true, true, authorities);

    TestingAuthenticationToken token = new TestingAuthenticationToken(userPrincipal, null, authorities);
    token.setAuthenticated(true);
    token.setDetails(claims);
    return token;
  }

}
