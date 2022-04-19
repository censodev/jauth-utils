package io.github.censodev.jauthutils.spring;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.github.censodev.jauthutils.core.TokenProvider;
import io.github.censodev.jauthutils.core.UserTest;
import io.github.censodev.jauthutils.core.api.AuthenticationException;
import io.github.censodev.jauthutils.core.api.AuthenticationFilterHook;
import io.github.censodev.jauthutils.core.api.Credential;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
class SpringAuthenticationFilterTest {
    SpringAuthenticationFilter<UserTest> filter;
    TokenProvider tokenProvider;
    AuthenticationFilterHook hook;
    MockHttpServletRequest req;
    MockHttpServletResponse res;
    MockFilterChain chain;

    @BeforeEach
    void setUp() {
        tokenProvider = new TokenProvider();
        req = new MockHttpServletRequest();
        res = new MockHttpServletResponse();
        chain = new MockFilterChain();
        SecurityContextHolder.clearContext();
    }

    @Test
    void expect401MissingAuthHeader() {
        hook = new AuthenticationFilterHook() {
            @Override
            public void beforeValidate(TokenProvider tokenProvider, String token) {

            }

            @Override
            public void afterValidateWell(Credential credential) {
                assertNull(credential);
            }

            @Override
            public void onError(Exception ex) {
                assertNull(ex);
            }

            @Override
            public void afterValidateFailed(AuthenticationException ex) {
                assertNull(SecurityContextHolder.getContext().getAuthentication());
                assertNotNull(ex);
                assertEquals(ex.getMessage(), "Invalid HTTP header for authentication");
            }
        };
        filter = new SpringAuthenticationFilter<>(tokenProvider, UserTest.class, hook);
        filter.doFilterInternal(req, res, chain);
    }

    @Test
    void expect401InvalidAuthHeaderValue() throws JsonProcessingException {
        String[] headerValues = new String[]{"", "aaa", tokenProvider.generateAccessToken(new UserTest())};
        hook = new AuthenticationFilterHook() {
            @Override
            public void beforeValidate(TokenProvider tokenProvider, String token) {

            }

            @Override
            public void afterValidateWell(Credential credential) {
                assertNull(credential);
            }

            @Override
            public void onError(Exception ex) {
                assertNull(ex);
            }

            @Override
            public void afterValidateFailed(AuthenticationException ex) {
                assertNull(SecurityContextHolder.getContext().getAuthentication());
                assertNotNull(ex);
                assertEquals(ex.getMessage(), "Invalid HTTP header for authentication");
            }
        };
        filter = new SpringAuthenticationFilter<>(tokenProvider, UserTest.class, hook);
        Arrays.stream(headerValues).forEach(val -> {
            req.addHeader(tokenProvider.getHeader(), val);
            chain = new MockFilterChain();
            filter.doFilterInternal(req, res, chain);
        });
    }

    @Test
    void expect200ValidToken() throws JsonProcessingException {
        String[] tokens = new String[]{
                tokenProvider.generateAccessToken(new UserTest()),
        };
        hook = new AuthenticationFilterHook() {
            @Override
            public void beforeValidate(TokenProvider tokenProvider, String token) {

            }

            @Override
            public void afterValidateWell(Credential credential) {
                assertNotNull(credential);
                assertNotNull(SecurityContextHolder.getContext().getAuthentication());
                assertTrue(SecurityContextHolder.getContext().getAuthentication().isAuthenticated());
            }

            @Override
            public void onError(Exception ex) {
                assertNull(ex);
            }

            @Override
            public void afterValidateFailed(AuthenticationException ex) {
                assertNull(ex);
            }
        };
        filter = new SpringAuthenticationFilter<>(tokenProvider, UserTest.class, hook);
        Arrays.stream(tokens).forEach(token -> {
            req.addHeader(tokenProvider.getHeader(), tokenProvider.getPrefix() + token);
            chain = new MockFilterChain();
            filter.doFilterInternal(req, res, chain);
        });
    }
}