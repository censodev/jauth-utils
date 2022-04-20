package io.github.censodev.jauthutils.core.api;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

public interface Credential {
    String getSubject();

    String getUsername();

    List<String> getAuthorities();

    default List<String> getNotNullAuthorities() {
        return Optional.ofNullable(getAuthorities())
                .orElse(Collections.emptyList());
    }
}
