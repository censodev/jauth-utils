# JAuth Utils
## 1. Introduction
JAuth Utils is a Java library that simplifies JWT authentication wrap [jjwt](https://github.com/jwtk/jjwt) and supports Spring Boot.

## 2. Prerequisites
* Java 8
* Maven 3

## 3. Installation
### Maven
```
<dependency>
    <groupId>io.github.censodev</groupId>
    <artifactId>jauth-utils</artifactId>
    <version>3.0.1</version>
</dependency>
```
### Gradle
```
implementation 'io.github.censodev:jauth-utils:3.0.1'
```

## 4. Usages
### 4.1. Define Authenticatable entity
```java
class User implements Credential {
    private String name = "name";

    // Getters, setters ...

    @Override
    public String getSubject() {
        return "subject";
    }

    @Override
    public String getUsername() {
        return "usn";
    }

    @Override
    public List<String> getAuthorities() {
        // must use modifiable list
        return new ArrayList<>(Arrays.asList("role_a", "role_b"));
    }
}
```
### 4.2. Initiate TokenProvider
```java
// With default config
TokenProvider tokenProvider = new TokenProvider();

// With builder
TokenProvider tokenProvider = TokenProvider.builder()
        .expireInMillisecond(3_600_000)
        .refreshTokenExpireInMillisecond(86_400_000)
        .secret("qwertyuiopasdfghjklzxcvbnm")
        .build();
```
### 4.3. Generate tokens
```java
String accessToken = tokenProvider.generateAccessToken(new User());
String refreshToken = tokenProvider.generateRefreshToken(new User());
```
### 4.4. Get credential from token
```java
User u = tokenProvider.getCredential(token, User.class);
```
### 4.5. Validate token
```java
try {
    tokenProvider.validateToken(token);
} catch (MalformedJwtException e) {
    e.printStackTrace();
} catch (ExpiredJwtException e) {
    e.printStackTrace();
} catch (UnsupportedJwtException e) {
    e.printStackTrace();
} catch (IllegalArgumentException e) {
    e.printStackTrace();
} catch (SignatureException e) {
    e.printStackTrace();
}
```
### 4.6. Spring Boot filter
#### 4.6.1. Initiate filter
```java
SpringAuthenticationFilter<User> filter = new SpringAuthenticationFilter<>(tokenProvider, User.class);

// with filter hook
AuthenticationFilterHook hook = new AuthenticationFilterHook() {
    @Override
    public void beforeValidate(TokenProvider tokenProvider, String token) {
        // do something before validate token
    }
    
    @Override
    public void afterValidateWell(Credential credential) {
        // do something after validate token successfully
    }
    
    @Override
    public void afterValidateFailed(JwtException ex) {
        // do something after validate token failed
    }

    @Override
    public void onError(Exception ex) {
        // do something when unexpected errors occur
    }
};
SpringAuthenticationFilter<User> filter = new SpringAuthenticationFilter<>(tokenProvider, User.class, hook);
```
#### 4.6.2. Configure security bean
```java
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .cors()
                    .and()
                .addFilterBefore(springAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .authorizeRequests()
                        .antMatchers(
                                "/api/auth/**"
                        ).permitAll()
                .anyRequest().authenticated();
    }

    @Bean
    public TokenProvider tokenProvider() {
        return TokenProvider.builder()
                .expireInMillisecond(3_600_000)
                .refreshTokenExpireInMillisecond(86_400_000)
                .secret("qwertyuiopasdfghjklzxcvbnm")
                .build();
    }
}
```
#### 4.6.3. Get credential from security context
```java
Optional<User> u = Optional.ofNullable((User) SecurityContextHolder.getContext().getAuthentication().getCredential());
```
