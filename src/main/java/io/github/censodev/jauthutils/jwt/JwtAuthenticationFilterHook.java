package io.github.censodev.jauthutils.jwt;

import io.jsonwebtoken.JwtException;

public interface JwtAuthenticationFilterHook {
    void beforeValidate(TokenProvider tokenProvider, String token);
    void afterValidateWell(Credentials credentials);
    void afterValidateFailed(JwtException ex);
    void onError(Exception ex);
}
