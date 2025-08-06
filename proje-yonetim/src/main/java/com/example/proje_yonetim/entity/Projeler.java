package com.example.proje_yonetim.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

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
    private Durum durum;

    public Projeler() {
    }

    public Projeler(Long id, String baslik, String aciklama, LocalDate baslangicTarihi, LocalDate bitisTarihi,
            Durum durum) {
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

    public Durum getDurum() {
        return durum;
    }

    public void setDurum(Durum durum) {
        this.durum = durum;
    }

    @ManyToMany
    @JoinTable(name = "proje_calisanlar", joinColumns = @JoinColumn(name = "proje_id"), inverseJoinColumns = @JoinColumn(name = "calisan_id"))
    private Set<Calisanlar> calisanlar = new HashSet<>();

    public Set<Calisanlar> getCalisanlar() {
        return calisanlar;
    }
}
