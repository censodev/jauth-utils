package censodev.lib.auth.utils.jwt;

import java.util.List;

public interface Credentials {
    Object getSubject();

    String getUsername();

    List<String> getAuthorities();
}
