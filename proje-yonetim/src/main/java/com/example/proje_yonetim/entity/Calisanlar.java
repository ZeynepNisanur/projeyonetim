package com.example.proje_yonetim.entity;

import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.*;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
//import com.fasterxml.jackson.annotation.JsonManagedReference;
//import com.fasterxml.jackson.annotation.JsonBackReference;

//import jakarta.validation.constraints.Email;     // E-posta formatı için  @Email le kullanılır
//import jakarta.validation.constraints.NotBlank;   // Boş olmaması için  //@NotBlank le kullanılır
//import jakarta.validation.constraints.NotNull;    // Null olmaması için  //NotNull la kullanılır 

@Table(name = "calisanlar")
@Entity
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class Calisanlar {

    @Id // primary key belirtir
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String ad;
    private String soyad;
    private String eposta;
    private String pozisyon;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "role_id")
    @JsonIgnoreProperties({ "users" })
    private Role role; // Çalışanın rolü (ADMIN/USER)

    @OneToOne(optional = true)
    @JoinColumn(name = "user_id", unique = true)
    @NotFound(action = NotFoundAction.IGNORE)
    @JsonIgnoreProperties({ "role" })
    private User user; // Kimlik doğrulama kaydı ile eşleşme

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
    @JsonIgnore
    private Set<Projeler> projeler = new HashSet<>();

    public Set<Projeler> getProjeler() {
        return projeler;
    }

    public void setProjeler(Set<Projeler> projeler) {
        this.projeler = projeler;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
