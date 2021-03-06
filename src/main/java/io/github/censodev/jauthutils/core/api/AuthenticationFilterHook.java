package io.github.censodev.jauthutils.core.api;

import io.github.censodev.jauthutils.core.TokenProvider;
import io.jsonwebtoken.JwtException;

public interface AuthenticationFilterHook {
    void beforeValidate(TokenProvider tokenProvider, String token);

    void afterValidateWell(Credential credential);

    void afterValidateFailed(JwtException ex);

    void onError(Exception ex);
}
