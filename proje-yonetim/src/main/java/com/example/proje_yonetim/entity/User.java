package com.example.proje_yonetim.entity;

import java.util.Set;

import java.util.HashSet;
import jakarta.persistence.*;

@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String useradi;
    private String sifre;
    private boolean enabled = true;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles = new HashSet<>();

    // kullanıcının birden fazla rolü olabileceği için role ilişkisi...^
    public Long getId() {
        return id;
    }

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

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

    @Column(name = "refresh_token")
    private String refreshToken;
}
