package com.example.proje_yonetim.controller;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
//import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class DashboardController {

    @GetMapping("/dashboard")
    public ResponseEntity<?> getDashboard(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Kullanıcı doğrulanmadı");
        }

        Map<String, Object> data = new HashMap<>();

        String useradi = authentication.getName();

        boolean isAdmin = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(role -> role.equals("ROLE_ADMIN"));
        boolean isUser = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(role -> role.equals("ROLE_USER"));

        if (isAdmin) {
            data.put("message",
                    "Admin paneline hoş geldiniz!" + " " + useradi + " " + data.put("modules", Arrays.asList(
                            "Çalışan Listesi ",
                            "Proje Yönetimi",
                            "Kullanıcı Yönetimi")));
            data.put("userType", "ADMIN");
        } else if (isUser) {
            data.put("message", useradi + " için özel dashboard!");
            data.put("modules", Arrays.asList(" Kendi Projelerim", "PRofil Ayarları"));
            data.put("userType", "USER");
        } else {
            data.put("message", "Hoş geldiniz," + useradi);
            data.put("modules", Arrays.asList("Genel Bilgiler"));
            data.put("userType", "UNKNOWN");
        }
        data.put("username", useradi);
        data.put("timestamp", System.currentTimeMillis());
        data.put("roles", authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toArray());
        return ResponseEntity.ok(data);
    }

    @GetMapping("/user-info")
    public ResponseEntity<?> getUserInfo(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Kullanıcı doğrulanmadı");
        }

        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("username", authentication.getName());
        userInfo.put("roles", authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toArray());
        userInfo.put("authenticated", true);

        return ResponseEntity.ok(userInfo);
    }
}
