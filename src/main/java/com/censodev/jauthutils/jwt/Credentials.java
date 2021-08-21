package com.censodev.jauthutils.jwt;

import java.util.List;

public interface Credentials {
    Object getSubject();

    String getUsername();

    List<String> getAuthorities();
}
