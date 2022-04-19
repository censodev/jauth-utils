package io.github.censodev.jauthutils.spring;

import io.github.censodev.jauthutils.core.Credential;
import io.github.censodev.jauthutils.core.AuthenticationFilterHook;
import io.github.censodev.jauthutils.core.TokenProvider;
import io.github.censodev.jauthutils.core.UserTest;
import io.jsonwebtoken.JwtException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

class SpringAuthenticationFilterTest {
    SpringAuthenticationFilter<UserTest> filter;
    TokenProvider tokenProvider;
    AuthenticationFilterHook hook;

    @BeforeEach
    void setUp() {
        tokenProvider = TokenProvider.builder()
                .header("Authorization")
                .prefix("Bearer ")
                .expireInMillisecond(86_400_000)
                .build();
        hook = new AuthenticationFilterHook() {
            @Override
            public void beforeValidate(TokenProvider tokenProvider, String token) {

            }

            @Override
            public void afterValidateWell(Credential credential) {

            }

            @Override
            public void onError(Exception ex) {

            }

            @Override
            public void afterValidateFailed(JwtException ex) {

            }
        };
        filter = new SpringAuthenticationFilter<>(tokenProvider, UserTest.class, hook);
    }

    @Test
    void expect401() {
        MockHttpServletRequest req = new MockHttpServletRequest();
        MockHttpServletResponse res = new MockHttpServletResponse();
        MockFilterChain chain = new MockFilterChain();
        filter.doFilterInternal(req, res, chain);
    }

    @Test
    void expect403() {
    }
}