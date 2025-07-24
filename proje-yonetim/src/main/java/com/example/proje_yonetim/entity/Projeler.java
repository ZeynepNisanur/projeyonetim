package com.example.proje_yonetim.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
public class Projeler {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String baslik;

    private String aciklama;

    private LocalDate baslangicTarihi;

    private LocalDate bitisTarihi;

    @Enumerated(EnumType.STRING)
    private Status durum;

    public Projeler() {
    }

    public Projeler(Long id, String baslik, String aciklama, LocalDate baslangicTarihi, LocalDate bitisTarihi,
            Status durum) {
        this.id = id;
        this.baslik = baslik;
        this.aciklama = aciklama;
        this.baslangicTarihi = baslangicTarihi;
        this.bitisTarihi = bitisTarihi;
        this.durum = durum;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBaslik() {
        return baslik;
    }

    public void setBaslik(String baslik) {
        this.baslik = baslik;
    }

    public String getAciklama() {
        return aciklama;
    }

    public void setAciklama(String aciklama) {
        this.aciklama = aciklama;
    }

    public LocalDate getBaslangicTarihi() {
        return baslangicTarihi;
    }

    public void setBaslangicTarihi(LocalDate baslangicTarihi) {
        this.baslangicTarihi = baslangicTarihi;
    }

    public LocalDate getBitisTarihi() {
        return bitisTarihi;
    }

    public void setBitisTarihi(LocalDate bitisTarihi) {
        this.bitisTarihi = bitisTarihi;
    }

    public Status getDurum() {
        return durum;
    }

    public void setDurum(Status durum) {
        this.durum = durum;
    }
}

enum Status {
    YENI,
    DEVAM_EDIYOR,
    ARA_VERILDI,
    TAMAMLANDI,
    IPTAL_EDILDI
}
