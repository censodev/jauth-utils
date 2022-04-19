package io.github.censodev.jauthutils.core.api;

import java.util.List;

public interface Credential {
    String getSubject();

    String getUsername();

    List<String> getAuthorities();
}
