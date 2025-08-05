package com.example.proje_yonetim.dto;

public class TokenRefreshRequest {
    private String refreshToken;

    public TokenRefreshRequest() {
    } // boş constructor

    public TokenRefreshRequest(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }
}
