package censodev.lib.auth.utils.jwt;

import lombok.Data;

import java.util.List;

@Data
public abstract class Credentials {
    private Object subject = "User Authorization";
    private List<String> authorities;
}
