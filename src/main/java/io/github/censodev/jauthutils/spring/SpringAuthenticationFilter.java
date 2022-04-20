package io.github.censodev.jauthutils.spring;

import io.github.censodev.jauthutils.core.TokenProvider;
import io.github.censodev.jauthutils.core.api.AuthenticationFilterHook;
import io.github.censodev.jauthutils.core.api.Credential;
import io.jsonwebtoken.JwtException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class SpringAuthenticationFilter<T extends Credential> extends OncePerRequestFilter {
    private final TokenProvider tokenProvider;
    private final Class<T> credentialClass;
    private final AuthenticationFilterHook hook;

    public SpringAuthenticationFilter(TokenProvider tokenProvider, Class<T> credentialClass) {
        this.tokenProvider = tokenProvider;
        this.credentialClass = credentialClass;
        this.hook = new AuthenticationFilterHook() {
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
    }

    public SpringAuthenticationFilter(TokenProvider tokenProvider, Class<T> credentialClass, AuthenticationFilterHook hook) {
        this.tokenProvider = tokenProvider;
        this.credentialClass = credentialClass;
        this.hook = hook;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) {

        String header = request.getHeader(tokenProvider.getHeader());

        if (header == null || !header.startsWith(tokenProvider.getPrefix())) {
            hook.afterValidateFailed(new JwtException("Invalid HTTP header for authentication"));
            try {
                chain.doFilter(request, response);
            } catch (IOException | ServletException e) {
                hook.onError(e);
            }
            return;
        }

        String token = header.replace(tokenProvider.getPrefix(), "");
        try {
            hook.beforeValidate(tokenProvider, token);
            tokenProvider.validateToken(token);
            T credential = tokenProvider.getCredential(token, credentialClass);
            List<SimpleGrantedAuthority> authorities = credential
                    .getAuthorities()
                    .stream().map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toList());
            String username = credential.getUsername();

            Authentication auth = new UsernamePasswordAuthenticationToken(username, credential, authorities);
            SecurityContextHolder.getContext().setAuthentication(auth);
            hook.afterValidateWell(credential);
        } catch (JwtException e) {
            hook.afterValidateFailed(e);
            SecurityContextHolder.clearContext();
        } catch (Exception e) {
            hook.onError(e);
            SecurityContextHolder.clearContext();
        }

        try {
            chain.doFilter(request, response);
        } catch (IOException | ServletException e) {
            hook.onError(e);
        }
    }
}
