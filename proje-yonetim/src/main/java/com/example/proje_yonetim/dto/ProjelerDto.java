package com.example.proje_yonetim.dto;

import java.util.List;

public class ProjelerDto {
    private Long id;
    private String baslik;
    private List<CalisanlarDto> calisanlar;

    public ProjelerDto(Long id, String baslik, List<CalisanlarDto> calisanlar) {
        this.id = id;
        this.baslik = baslik;
        this.calisanlar = calisanlar;
    }

    // Getter
    public Long getId() {
        return id;
    }

    public String getBaslik() {
        return baslik;
    }

    public List<CalisanlarDto> getCalisanlar() {
        return calisanlar;
    }

}
