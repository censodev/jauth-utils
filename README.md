# JAuth Utils
## Introduction
JAuth Utils is a Java library that simplifies JWT authentication wrap [jjwt](https://github.com/jwtk/jjwt) and supports Spring Boot.

## Prerequisites
* Java 8
* Maven 3

## Installation
### Config in pom.xml
```
<dependency>
    <groupId>io.github.censodev</groupId>
    <artifactId>jauth-utils</artifactId>
    <version>1.0.1</version>
</dependency>
```
### Install with Maven CLI
```
mvn install
```

## Usages
### Define Authenticatable entity
```java
class User implements Credentials {
    private String name = "name";

    // Getters, setters ...

    @Override
    public Object getSubject() {
        return "subject";
    }

    @Override
    public String getUsername() {
        return "usn";
    }

    @Override
    public List<String> getAuthorities() {
        return new ArrayList<>(Arrays.asList("role_a", "role_b"));
    }
}
```
### Initiate TokenProvider
```java
// With default config
TokenProvider tokenProvider = new TokenProvider();

// With builder
TokenProvider tokenProvider = TokenProvider.builder()
        .header("Authorization")
        .prefix("Bearer ")
        .expiration(86_400_000)
        .build();
```
### Generate token
```java
String token = tokenProvider.generateToken(new User());
```
### Get credentials from token
```java
User u = tokenProvider.getCredentials(token, User.class);
```
### Validate token
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
### Config filter in Spring Security example
```java
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .cors()
                    .and()
                .addFilterBefore(new JwtAuthenticationFilter<>(tokenProvider(), User.class), UsernamePasswordAuthenticationFilter.class)
                .authorizeRequests()
                        .antMatchers(
                                "/api/auth/**"
                        ).permitAll()
                        .anyRequest().authenticated();
    }

    @Bean
    public TokenProvider tokenProvider() {
        return TokenProvider.builder()
                .header("Authorization")
                .prefix("Bearer ")
                .expiration(86_400_000)
                .build();
    }
}
```
### Get credentials from security context
```java
Optional<User> u = Optional.ofNullable((User) SecurityContextHolder.getContext().getAuthentication().getCredentials());
```
