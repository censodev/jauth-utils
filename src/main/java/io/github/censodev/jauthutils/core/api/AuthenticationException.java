package io.github.censodev.jauthutils.core.api;

import io.jsonwebtoken.JwtException;

public class AuthenticationException extends JwtException {
    public AuthenticationException(String message) {
        super(message);
    }

    public AuthenticationException(String message, Throwable cause) {
        super(message, cause);
    }
}
