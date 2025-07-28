package com.example.proje_yonetim.entity;

import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.*;

//import jakarta.validation.constraints.Email;     // E-posta formatı için  @Email le kullanılır
//import jakarta.validation.constraints.NotBlank;   // Boş olmaması için  //@NotBlank le kullanılır
//import jakarta.validation.constraints.NotNull;    // Null olmaması için  //NotNull la kullanılır 

@Table(name = "calisanlar")
@Entity
public class Calisanlar {

    @Id // primary key belirtir
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String ad;
    private String soyad;
    private String eposta;
    private String pozisyon;

    public Calisanlar() {
    }

    public Calisanlar(Long id, String ad, String soyad, String eposta, String pozisyon) {
        this.id = id;
        this.ad = ad;
        this.soyad = soyad;
        this.eposta = eposta;
        this.pozisyon = pozisyon;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAd() {
        return ad;
    }

    public void setAd(String ad) {
        this.ad = ad;
    }

    public String getSoyad() {
        return soyad;
    }

    public void setSoyad(String soyad) {
        this.soyad = soyad;
    }

    public String getAdSoyad() {
        return ad + " " + soyad;
    }

    public String getEposta() {
        return eposta;
    }

    public void setEposta(String eposta) {
        this.eposta = eposta;
    }

    public String getPozisyon() {
        return pozisyon;
    }

    public void setPozisyon(String pozisyon) {
        this.pozisyon = pozisyon;
    }

    @ManyToMany(mappedBy = "calisanlar")
    private Set<Projeler> projeler = new HashSet<>();
}