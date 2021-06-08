# JAuth Utils
## Introduction
JAuth Utils is a Java library simplify JWT authentication base on [jjwt](https://github.com/jwtk/jjwt) and supports for Spring Boot

## Prerequisites
* Java 8
* Maven 3

## Installation
### Authenticate to GitHub Packages
You can authenticate to GitHub Packages with Apache Maven by editing your ~/.m2/settings.xml file to include your personal access token. Create a new ~/.m2/settings.xml file if one doesn't exist.
Replace USERNAME with your GitHub username, and TOKEN with your personal access token.
```
<?xml version="1.0" encoding="UTF-8"?>
<settings xmlns="http://maven.apache.org/SETTINGS/1.0.0"
          xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
          xsi:schemaLocation="http://maven.apache.org/SETTINGS/1.0.0 http://maven.apache.org/xsd/settings-1.0.0.xsd">
    <activeProfiles>
        <activeProfile>central</activeProfile>
        <activeProfile>github</activeProfile>
    </activeProfiles>

    <profiles>
        <profile>
            <id>central</id>
            <repositories>
                <repository>
                    <id>central</id>
                    <url>https://repo1.maven.org/maven2</url>
                </repository>
            </repositories>
        </profile>
        <profile>
            <id>github</id>
            <repositories>
                <repository>
                    <id>github.censodev</id>
                    <url>https://maven.pkg.github.com/censodev/*</url>
                    <snapshots>
                        <enabled>true</enabled>
                    </snapshots>
                </repository>
            </repositories>
        </profile>
    </profiles>

    <servers>
        <server>
            <id>github</id>
            <username>USERNAME</username>
            <password>TOKEN</password>
        </server>
    </servers>
</settings>
```
### Config in pom.xml
```
<dependency>
    <groupId>censodev.lib</groupId>
    <artifactId>jauth-utils</artifactId>
    <version>1.0</version>
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
                .addFilterBefore(new JwtAuthenticationFilter(tokenProvider(), User.class), UsernamePasswordAuthenticationFilter.class)
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
                .build();;
    }
}
```
### Get credentials from security context
```java
Optional<User> u = Optional.ofNullable((User) SecurityContextHolder.getContext().getAuthentication().getCredentials());
```