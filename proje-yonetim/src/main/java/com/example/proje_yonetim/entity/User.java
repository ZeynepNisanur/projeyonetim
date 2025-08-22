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

    @Column(unique = true, nullable = false)
    private String useradi;
    @Column(nullable = false)
    private String sifre;
    private boolean enabled = true;

    @ManyToMany(fetch = FetchType.EAGER, cascade = { CascadeType.MERGE, CascadeType.PERSIST })
    @JoinTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "role_id"), uniqueConstraints = @UniqueConstraint(columnNames = {
            "user_id", "role_id" }))
    private Set<Role> roles = new HashSet<>();

    public User() {
    }

    public User(String useradi, String sifre) {
        this.useradi = useradi;
        this.sifre = sifre;
    }

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

    // Role ekleme metodu
    public void addRole(Role role) {
        this.roles.add(role);
        role.getUsers().add(this);
    }

    // Role çıkarma metodu
    public void removeRole(Role role) {
        this.roles.remove(role);
        role.getUsers().remove(this);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;
        User user = (User) obj;
        return useradi != null && useradi.equals(user.useradi);
    }

    @Override
    public int hashCode() {
        return useradi != null ? useradi.hashCode() : 0;
    }
}