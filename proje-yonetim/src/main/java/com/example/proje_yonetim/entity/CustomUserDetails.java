package com.example.proje_yonetim.entity;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Collection;
//import java.util.List;
import java.util.stream.Collectors;

public class CustomUserDetails implements UserDetails {

    private final User user;

    public CustomUserDetails(User user) {
        this.user = user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority(role.getName().toUpperCase()))
                .collect(Collectors.toSet());
    }

    @Override
    public String getPassword() {
        return user.getSifre();
    }

    @Override
    public String getUsername() {
        return user.getUseradi();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    /*
     * @Override
     * public UserDetails loadUserByUsername(String useradi) throws
     * UsernameNotFoundException {
     * User user = userRepository.findByUseradi(useradi);
     * if (user == null) {
     * throw new UsernameNotFoundException("Kullanıcı bulunamadı: " + useradi);
     * }
     * List<GrantedAuthority> authorities = user.getRoles().stream()
     * .map(role -> new SimpleGrantedAuthority("ROLE_" + role.getName()))
     * .collect(Collectors.toList());
     * 
     * return new org.springframework.security.core.userdetails.User(
     * user.getUseradi(), user.getSifre(), authorities);
     * }
     */
}
