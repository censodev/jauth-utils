package io.github.censodev.jauthutils.core;

import java.util.List;

public interface Credential {
    String getSubject();

    String getUsername();

    List<String> getAuthorities();
}
