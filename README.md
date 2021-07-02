# spring-security-oauth2-test
[![](https://jitpack.io/v/ahunigel/spring-security-oauth2-test.svg)](https://jitpack.io/#ahunigel/spring-security-oauth2-test)

This library is helpful for integration test based on spring security, especially oauth2 for resource server, works 
with `MockMvc`.

It enhanced spring-security-test by mock an OAuth2 client or on behalf of user.

Attach Map-based claims to mocked user as authentication details, the claims can be extracted from bearer jwt token.

_Note: Most code came from the open network. I refactor and enhanced the code, then we have this java-library._

## Features
- `@WithMockOAuth2Client`
- `@WithMockOAuth2User`
    - mock an oauth2 user, attach claims to OAuth2Authentication details
- `@AttachClaims`
    - attach Map-based claims to current authentication, should work with `@WithMockUser`
- `@WithMockUserAndClaims`
    - enhanced `@WithMockUser`, attach Map-based claims as authentication details
    - equal to `@WithMockUser` + `@AttachClaims`
- `@WithToken`
    - add `bearer` token to request header to extract a `PreAuthenticatedAuthenticationToken`,
    load existing OAuth2Authentication from SecurityContext
    - require `@MockTokenServices` on test class
- `@ResourcesNonStateless`
    - allow non token-based authentication to access oauth2 resources

## How to use

### Step 1. Add the JitPack repository to your build file
```groovy
allprojects {
    repositories {
        ...
        maven { url 'https://jitpack.io' }
    }
}
```
## Step 2. Add the dependency
```groovy
dependencies {
    implementation 'com.github.ahunigel:spring-security-oauth2-test:{version}'
}
```
_Refer to https://jitpack.io/#ahunigel/spring-security-oauth2-test for details._

## Step 3. Write tests
```java
@WithMockOAuth2User(
    client = @WithMockOAuth2Client(
      clientId = "custom-client",
      scope = {"custom-scope", "other-scope"},
      authorities = {"custom-authority", "ROLE_CUSTOM_CLIENT"}),
    user = @WithMockUser(
      username = "custom-username",
      authorities = {"custom-user-authority"}),
    claims = @AttachClaims({
      @Claim(name = "user_id", value = "6", type = Long.class),
      @Claim(name = "role_id", value = "1"),
      @Claim(name = "is_social_user", value = "false")
    })
}
```
or
```java
@AttachClaims(value = {
    @Claim(name = "user_id", value = "6", type = Long.class),
    @Claim(name = "role_id", value = "1"),
    @Claim(name = "is_social_user", value = "false")},
    claims = {"email:ahunigel@gmail.com", "user_name=ahunigel"}
)
@WithMockUser()
```
or
```java
@WithMockUserAndClaims(
    @AttachClaims(value = {
        @Claim(name = "user_id", value = "6", type = Long.class),
        @Claim(name = "role_id", value = "1"),
        @Claim(name = "is_social_user", value = "false")},
        claims = {"email:ahunigel@gmail.com", "user_name=ahunigel"}
    )
)
```

## References
- [How to get @WebMvcTest work with OAuth?](https://stackoverflow.com/questions/48540152/how-to-get-webmvctest-work-with-oauth)
- [Faking OAuth2 Single Sign-on in Spring](http://engineering.pivotal.io/post/faking_oauth_sso/)
- [@WithSecurityContext](https://docs.spring.io/spring-security/site/docs/current/reference/html/test-method.html#test-method-withsecuritycontext)
- [Spring MVC Test Integration](https://docs.spring.io/spring-security/site/docs/current/reference/html/test-mockmvc.html)
- [OAuth2 Autoconfig](https://docs.spring.io/spring-security-oauth2-boot/docs/current/reference/htmlsingle/)
- [Retrieve User Information in Spring Security](https://www.baeldung.com/get-user-in-spring-security)
- [Spring Security OAuth](https://projects.spring.io/spring-security-oauth/docs/Home.html)
- [Use @WithMockUser with @SpringBootTest inside an oAuth2 Resource Server Application](https://stackoverflow.com/questions/41824885/use-withmockuser-with-springboottest-inside-an-oauth2-resource-server-applic)

## See Also
- [spring-security-oauth2-test](https://github.com/ahunigel/spring-security-oauth2-test)
- [spring-toolkit](https://github.com/ahunigel/spring-toolkit)
- [spring-test-toolkit](https://github.com/ahunigel/spring-test-toolkit)

## TODOs

- Attach claims for @WithMockOAuth2Client/@WithMockOAuth2User via @AttachClaims 
- Migrate Spring Security OAuth 2.x application to Spring Security 5.2
- Add support for `RestTemplate`
- Add unit test
