package censodev.lib.auth.utils;

import censodev.lib.auth.utils.jwt.Credentials;
import censodev.lib.auth.utils.jwt.TokenProvider;
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
        TokenProvider tokenProvider = new TokenProvider();
        String token = tokenProvider.generateToken(new User());
        User u = tokenProvider.getCredentials(token, User.class);
        System.out.println(u.toString());
        System.out.println(u.getSubject());
        System.out.println(u.getUsername());
        System.out.println(u.getAuthorities().toString());
    }
}
