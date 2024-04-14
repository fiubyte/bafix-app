package com.fiubyte.bafix.entities;

import androidx.annotation.NonNull;

public class LoginResult {
    private String token;

    public LoginResult(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }

    @NonNull
    @Override
    public String toString() {
        return "LoginResult{" +
                "token='" + token + '\'' +
                '}';
    }
}
