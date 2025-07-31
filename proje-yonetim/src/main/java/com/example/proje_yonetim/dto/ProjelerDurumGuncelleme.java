package com.example.proje_yonetim.dto;

import com.example.proje_yonetim.entity.Durum;

public class ProjelerDurumGuncelleme {
    private Durum durum;

    public ProjelerDurumGuncelleme() {
        // boş constructor
    }

    public ProjelerDurumGuncelleme(Durum durum) {
        this.durum = durum;
    }

    public Durum getDurum() {
        return durum;
    }

    public void setDurum(Durum durum) {
        this.durum = durum;
    }
}
