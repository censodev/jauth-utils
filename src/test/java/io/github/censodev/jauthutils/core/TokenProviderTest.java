package io.github.censodev.jauthutils.core;

import io.github.censodev.jauthutils.core.api.Credential;
import io.jsonwebtoken.SignatureAlgorithm;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

class TokenProviderTest {
    TokenProvider tokenProvider;
    Credential user;

    @BeforeEach
    void setUp() {
        tokenProvider = new TokenProvider();
        user = new UserTest(Arrays.asList("ROLE_ADMIN", "ROLE_CUSTOMER"), "admin");
    }

    @Test
    void generateAccessToken() {
        assertDoesNotThrow(() -> tokenProvider.generateAccessToken(user));
    }

    @Test
    void generateRefreshToken() {
        assertDoesNotThrow(() -> tokenProvider.generateRefreshToken(user));
    }

    @Test
    void getCredentials() {
        assertDoesNotThrow(() -> {
            String token = tokenProvider.generateAccessToken(new UserTest());
            tokenProvider.getCredential(token, UserTest.class);
        });
    }

    @Test
    void validateToken() {
    }

    @Test
    void getHeader() {
        assertEquals(tokenProvider.getHeader(), "Authorization");
    }

    @Test
    void getPrefix() {
        assertEquals(tokenProvider.getPrefix(), "Bearer ");
    }

    @Test
    void getExpiration() {
        assertEquals(tokenProvider.getExpireInMillisecond(), 3_600_000);
    }

    @Test
    void getSecret() {
        assertEquals(tokenProvider.getSecret(), "qwertyuiopasdfghjklzxcvbnm1!2@3#4$5%6^7&8*9(0)-_=+");
    }

    @Test
    void getRefreshTokenExpireInMillisecond() {
        assertEquals(tokenProvider.getRefreshTokenExpireInMillisecond(), 86_400_000);
    }

    @Test
    void getCredentialClaimKey() {
        assertEquals(tokenProvider.getCredentialClaimKey(), "credential");
    }

    @Test
    void getSignatureAlgorithm() {
        assertEquals(tokenProvider.getSignatureAlgorithm(), SignatureAlgorithm.HS256);
    }
}