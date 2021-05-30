package censodev.lib.auth.utils.jwt;

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
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        // 1. get the authentication header. Tokens are supposed to be passed in the authentication header
        String header = request.getHeader(tokenProvider.getHeader());

        // 2. validate the header and check the prefix
        if (header == null || !header.startsWith(tokenProvider.getPrefix())) {
            chain.doFilter(request, response);
            return;
        }

        // If there is no token provided and hence the user won't be authenticated.
        // It's Ok. Maybe the user accessing a public path or asking for a token.

        // All secured paths that needs a token are already defined and secured in config class.
        // And If user tried to access without access token, then he won't be authenticated and an exception will be thrown.

        // 3. Get the token
        String token = header.replace(tokenProvider.getPrefix(), "");
        try {
            tokenProvider.validateToken(token);
            T credentials = tokenProvider.getCredentials(token, tClass);
            List<SimpleGrantedAuthority> authorities = credentials
                    .getAuthorities()
                    .stream().map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toList());
            String username = credentials.getUsername();

            // 4. Create auth object
            // UsernamePasswordAuthenticationToken: A built-in object, used by spring to represent the current authenticated / being authenticated user.
            // It needs a list of authorities, which has type of GrantedAuthority interface, where SimpleGrantedAuthority is an implementation of that interface
            Authentication auth = new UsernamePasswordAuthenticationToken(username, credentials, authorities);
            SecurityContextHolder.getContext().setAuthentication(auth);
        } catch (Exception e) {
            SecurityContextHolder.clearContext();
        }

        // go to the next filter in the filter chain
        chain.doFilter(request, response);
    }
}
