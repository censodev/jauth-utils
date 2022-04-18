package io.github.censodev.jauthutils.jwt;

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

public class JwtAuthenticationFilter<T extends Credentials> extends OncePerRequestFilter {
    private final TokenProvider tokenProvider;
    private final Class<T> credentialClass;
    private final JwtAuthenticationFilterHook hook;

    public JwtAuthenticationFilter(TokenProvider tokenProvider, Class<T> credentialClass) {
        this.tokenProvider = tokenProvider;
        this.credentialClass = credentialClass;
        this.hook = new JwtAuthenticationFilterHook() {
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
    }

    public JwtAuthenticationFilter(TokenProvider tokenProvider, Class<T> credentialClass, JwtAuthenticationFilterHook hook) {
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
            T credentials = tokenProvider.getCredentials(token, credentialClass);
            List<SimpleGrantedAuthority> authorities = credentials
                    .getAuthorities()
                    .stream().map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toList());
            String username = credentials.getUsername();

            Authentication auth = new UsernamePasswordAuthenticationToken(username, credentials, authorities);
            SecurityContextHolder.getContext().setAuthentication(auth);
            hook.afterValidateWell(credentials);
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
