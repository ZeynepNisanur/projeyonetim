package com.example.proje_yonetim.dto;

public class CreateUserRequest {
    private String kullaniciadi;
    private String sifre;
    private String eposta;

    public CreateUserRequest() {
    }

    public CreateUserRequest(String kullaniciadi, String sifre, String eposta) {
        this.kullaniciadi = kullaniciadi;
        this.sifre = sifre;
        this.eposta = eposta;
    }

    // Getters and Setters
    public String getKullaniciadi() {
        return kullaniciadi;
    }

    public void setKullaniciadi(String kullaniciadi) {
        this.kullaniciadi = kullaniciadi;
    }

    public String getSifre() {
        return sifre;
    }

    public void setSifre(String sifre) {
        this.sifre = sifre;
    }

    public String getEposta() {
        return eposta;
    }

    public void setEposta(String eposta) {
        this.eposta = eposta;
    }

}
