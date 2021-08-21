package com.censodev.jauthutils.jwt;

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
    private final Class<T> tClass;

    public JwtAuthenticationFilter(TokenProvider tokenProvider, Class<T> tClass) {
        this.tokenProvider = tokenProvider;
        this.tClass = tClass;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain)
            throws ServletException, IOException {

        String header = request.getHeader(tokenProvider.getHeader());

        if (header == null || !header.startsWith(tokenProvider.getPrefix())) {
            chain.doFilter(request, response);
            return;
        }

        String token = header.replace(tokenProvider.getPrefix(), "");
        try {
            tokenProvider.validateToken(token);
            T credentials = tokenProvider.getCredentials(token, tClass);
            List<SimpleGrantedAuthority> authorities = credentials
                    .getAuthorities()
                    .stream().map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toList());
            String username = credentials.getUsername();

            Authentication auth = new UsernamePasswordAuthenticationToken(username, credentials, authorities);
            SecurityContextHolder.getContext().setAuthentication(auth);
        } catch (Exception e) {
            SecurityContextHolder.clearContext();
        }

        chain.doFilter(request, response);
    }
}
