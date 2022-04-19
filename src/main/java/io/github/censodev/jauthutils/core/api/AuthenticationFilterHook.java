package io.github.censodev.jauthutils.core.api;

import io.github.censodev.jauthutils.core.TokenProvider;

public interface AuthenticationFilterHook {
    void beforeValidate(TokenProvider tokenProvider, String token);

    void afterValidateWell(Credential credential);

    void afterValidateFailed(AuthenticationException ex);

    void onError(Exception ex);
}
