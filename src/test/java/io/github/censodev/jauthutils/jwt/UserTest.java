package io.github.censodev.jauthutils.jwt;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

class UserTest implements Credentials {
    private List<String> authorities;
    private String username;
    private Instant createdAt;

    UserTest(List<String> authorities, String username) {
        this.authorities = authorities;
        this.username = username;
        createdAt = Instant.now();
    }

    UserTest() {
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setAuthorities(List<String> authorities) {
        this.authorities = authorities;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public Object getSubject() {
        return username;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public List<String> getAuthorities() {
        return new ArrayList<>(authorities);
    }
}
