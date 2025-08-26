package com.example.proje_yonetim.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "useradi", unique = true, nullable = true)
    private String username;

    @Column(nullable = false)
    private String sifre;

    @Column(unique = true, nullable = true)
    private String eposta;

    @Column(nullable = false)
    private boolean enabled = true; // Spring Security için gerekli

    // Bir kullanıcının sadece bir rolü olabilir (Many-to-One)
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;

    public User() {
    }

    public User(String username, String sifre, String eposta) {
        this.username = username;
        this.sifre = sifre;
        this.eposta = eposta;
        this.enabled = true;
    }

    public User(String username, String sifre, String eposta, Role role) {
        this.username = username;
        this.sifre = sifre;
        this.eposta = eposta;
        this.role = role;
        this.enabled = true;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
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

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    // Convenience method - rolün adını almak için
    public Role.RoleName getRoleName() {
        return role != null ? role.getName() : null;
    }

    // Role kontrolü için yardımcı metodlar
    public boolean isAdmin() {
        return role != null && role.getName() == Role.RoleName.ADMIN;
    }

    public boolean isUser() {
        return role != null && role.getName() == Role.RoleName.USER;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;
        User user = (User) obj;
        return username != null && username.equals(user.username);
    }

    @Override
    public int hashCode() {
        return username != null ? username.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", eposta='" + eposta + '\'' +
                ", enabled=" + enabled +
                ", role=" + (role != null ? role.getName() : null) +
                '}';
    }
}