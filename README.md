# spring-security-oauth2-test
[![](https://jitpack.io/v/ahunigel/spring-security-oauth2-test.svg)](https://jitpack.io/#ahunigel/spring-security-oauth2-test)

This library is helpful for integration test based on spring security, especially oauth2 for resource server, works 
with `MockMvc`.

It enhanced spring-security-test by mock an OAuth2 client or on behalf of user.

Attach Map-based claims to mocked user as authentication details, the claims can be extracted from bearer jwt token.

_Note: Most code came from the open network. I refactor and enhanced the code, then we have this java-library._

## Features
- @WithMockOAuth2Client
- @WithMockOAuth2User
    - mock an oauth2 user, attach claims to OAuth2Authentication details
- @AttachClaims
    - attach Map-based claims to current authentication, should work with @WithMockUser
- @WithMockUserAndClaims
    - enhanced @WithMockUser, attach Map-based claims as authentication details
    - equal to @WithMockUser + @AttachClaims

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
    implementation 'com.github.ahunigel:spring-security-oauth2-test:master-SNAPSHOT'
}
```
## Step 3. Write test
```java
@WithMockOAuth2User(
      client = @WithMockOAuth2Client(
          clientId = "custom-client",
          scope = {"custom-scope", "other-scope"},
          authorities = {"custom-authority", "ROLE_CUSTOM_CLIENT"}),
      user = @WithMockUser(
          username = "custom-username",
          authorities = {"custom-user-authority"}),
      claims = {
          @Claim(name = "user_id", value = "6", type = Long.class),
          @Claim(name = "role_id", value = "1"),
          @Claim(name = "is_social_user", value = "false")
      })
```
or
```java
@AttachClaims(value = {
  @Claim(name = "user_id", value = "6", type = Long.class),
  @Claim(name = "role_id", value = "1"),
  @Claim(name = "is_social_user", value = "false")
}, claims = {ROLE_NAME, "ADMIN"})
```
Refer to https://jitpack.io/#ahunigel/spring-security-oauth2-test for details.

## TODOs

1. Mock full oauth2 process, add `bearer` token to request header to extract a `PreAuthenticatedAuthenticationToken`

2. For oauth2 request, add ability to set ResourceServerSecurityConfigurer.stateless to false, maybe an 
annotation like `@ResourceStateLess(false)`

3. Add support for `RestTemplate`
