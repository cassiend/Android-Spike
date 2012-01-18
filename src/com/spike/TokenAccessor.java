package com.spike;

public class TokenAccessor implements StoredCredentials{

    private static final String ACCESS_TOKEN = "access_token";
    private String token = null;

    public TokenAccessor(String token) {
        this.token = token;
    }

    @Override
    public String current() {
        return this.token;
    }

    @Override
    public void write(String token) {
        this.token = token;
    }

    @Override
    public void clear() {
        this.token = null;
    }
}