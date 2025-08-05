package com.example.proje_yonetim.dto;

public class AuthRequestDto {
    private String useradi;
    private String sifre;

    public AuthRequestDto() {
    }

    public AuthRequestDto(String useradi, String sifre) {
        this.useradi = useradi;
        this.sifre = sifre;
    }

    // Getters ve Setters
    public String getUseradi() {
        return useradi;
    }

    public void setUseradi(String useradi) {
        this.useradi = useradi;
    }

    public String getSifre() {
        return sifre;
    }

    public void setSifre(String sifre) {
        this.sifre = sifre;
    }
}