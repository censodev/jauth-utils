package censodev.lib.jauthutils;

import censodev.lib.jauthutils.jwt.Credentials;
import censodev.lib.jauthutils.jwt.TokenProvider;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;
import lombok.Data;
import lombok.ToString;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Test {
    @Data
    @ToString(callSuper = true)
    static class User implements Credentials {
        private String name = "name";

        // Getters, setters ...

        @Override
        public Object getSubject() {
            return "subject";
        }

        @Override
        public String getUsername() {
            return "usn";
        }

        @Override
        public List<String> getAuthorities() {
            return new ArrayList<>(Arrays.asList("role_a", "role_b"));
        }
    }
    public static void main(String[] args) {
        TokenProvider tokenProvider = TokenProvider.builder()
                .header("Authorization")
                .prefix("Bearer ")
                .expiration(86_400_000)
                .build();
        String token = tokenProvider.generateToken(new User());
        User u = tokenProvider.getCredentials(token, User.class);
        try {
            tokenProvider.validateToken(token);
        } catch (MalformedJwtException | ExpiredJwtException | UnsupportedJwtException | IllegalArgumentException | SignatureException e) {
            e.printStackTrace();
        }
        System.out.println(u.toString());
        System.out.println(u.getSubject());
        System.out.println(u.getUsername());
        System.out.println(u.getAuthorities().toString());
    }
}
