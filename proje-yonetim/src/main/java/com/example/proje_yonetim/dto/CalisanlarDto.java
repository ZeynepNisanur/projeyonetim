package com.example.proje_yonetim.dto;

public class CalisanlarDto {
    private Long id;
    private String ad;
    private String soyad;

    public CalisanlarDto(Long id, String adSoyad) {
        this.id = id;
        // this.ad = ad;
        // this.soyad = soyad;
    }

    // Getters + setter bölümünün olmaması immutable olmaasını sağlamak için.
    public Long getId() {
        return id;
    }

    public String getAdSoyad() {
        return ad + " " + soyad;
    }

    // String için boş constructor
    public CalisanlarDto() {
    }

}