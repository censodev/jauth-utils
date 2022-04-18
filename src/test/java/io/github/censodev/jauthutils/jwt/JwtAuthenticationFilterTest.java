package io.github.censodev.jauthutils.jwt;

import io.jsonwebtoken.JwtException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

class JwtAuthenticationFilterTest {
    JwtAuthenticationFilter<UserTest> filter;
    TokenProvider tokenProvider;
    JwtAuthenticationFilterHook hook;

    @BeforeEach
    void setUp() {
        tokenProvider = TokenProvider.builder()
                .header("Authorization")
                .prefix("Bearer ")
                .expiration(86_400_000)
                .build();
        hook = new JwtAuthenticationFilterHook() {
            @Override
            public void beforeValidate(TokenProvider tokenProvider, String token) {

            }

            @Override
            public void afterValidateWell(Credentials credentials) {

            }

            @Override
            public void onError(Exception ex) {

            }

            @Override
            public void afterValidateFailed(JwtException ex) {

            }
        };
        filter = new JwtAuthenticationFilter<>(tokenProvider, UserTest.class, hook);
    }

    @Test
    void expect401() {
//        MockHttpServletRequest req = new MockHttpServletRequest();
//        MockHttpServletResponse res = new MockHttpServletResponse();
//        MockFilterChain chain = new MockFilterChain();
//        filter.doFilterInternal(req, res, chain);
    }

    @Test
    void expect403() {
    }
}