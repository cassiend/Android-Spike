package com.spike;

public interface StoredCredentials {

    String current();
    void write(String response);
    void clear();
}
