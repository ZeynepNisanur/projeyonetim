package com.example.proje_yonetim.config;

import com.example.proje_yonetim.entity.Role;
import com.example.proje_yonetim.entity.User;
import com.example.proje_yonetim.repository.RoleRepository;
import com.example.proje_yonetim.repository.CalisanlarRepository;
import com.example.proje_yonetim.entity.Calisanlar;
import com.example.proje_yonetim.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.Optional;

@Configuration
public class DataInitializer {

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private CalisanlarRepository calisanlarRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @PostConstruct
    public void init() {
        // Eski rol değerlerini (ROLE_ADMIN/ROLE_USER) enum ile uyumlu değerlere
        // dönüştür
        try {
            int updatedAdmin = jdbcTemplate.update("UPDATE roles SET name = 'ADMIN' WHERE name = 'ROLE_ADMIN'");
            int updatedUser = jdbcTemplate.update("UPDATE roles SET name = 'USER' WHERE name = 'ROLE_USER'");
            if (updatedAdmin > 0 || updatedUser > 0) {
                System.out.println(
                        ">> Legacy role değerleri dönüştürüldü. ADMIN: " + updatedAdmin + ", USER: " + updatedUser);
            }
        } catch (Exception e) {
            System.out.println(">> Rol isim dönüşümü SQL sırasında sorun: " + e.getMessage());
        }

        // Roller için yinelenen kayıtları temizle (aynı name'e sahip birden fazla role
        // varsa en düşük id'li kalsın)
        try {
            int removed = jdbcTemplate.update(
                    "DELETE FROM roles r USING roles r2 WHERE r.name = r2.name AND r.id > r2.id");
            if (removed > 0) {
                System.out.println(">> Yinelenen role kayıtları temizlendi: " + removed);
            }
        } catch (Exception e) {
            System.out.println(">> Role duplicate cleanup sırasında sorun: " + e.getMessage());
        }

        // Kullanıcı adları boşsa useradi kolonuna id tabanlı değer ata (geçici
        // güvenlik)
        try {
            int updatedUsers = jdbcTemplate.update(
                    "UPDATE users SET useradi = CONCAT('user_', id) WHERE useradi IS NULL OR useradi = ''");
            if (updatedUsers > 0) {
                System.out.println(">> Boş useradi güncellendi: " + updatedUsers);
            }
        } catch (Exception e) {
            System.out.println(">> useradi doldurma SQL sırasında sorun: " + e.getMessage());
        }
        // ADMIN rolünü oluştur (eğer yoksa)
        Role adminRole;
        Optional<Role> existingAdminRole = roleRepository.findByName(Role.RoleName.ADMIN);
        if (existingAdminRole.isEmpty()) {
            adminRole = new Role();
            adminRole.setName(Role.RoleName.ADMIN);
            adminRole = roleRepository.save(adminRole);
            System.out.println(">> ADMIN rolü oluşturuldu");
        } else {
            adminRole = existingAdminRole.get();
            System.out.println(">> ADMIN rolü zaten mevcut");
        }

        // USER rolünü oluştur (eğer yoksa)
        Optional<Role> existingUserRole = roleRepository.findByName(Role.RoleName.USER);
        if (existingUserRole.isEmpty()) {
            Role userRole = new Role();
            userRole.setName(Role.RoleName.USER);
            roleRepository.save(userRole);
            System.out.println(">> USER rolü oluşturuldu");
        } else {
            System.out.println(">> USER rolü zaten mevcut");
        }

        // Admin kullanıcısı oluştur (eğer yoksa)
        Optional<User> existingUser = userRepository.findByUsername("nisa");
        if (existingUser.isEmpty()) {
            User user = new User();
            user.setUsername("nisa");
            user.setSifre(passwordEncoder.encode("1234"));
            user.setEnabled(true);
            user.setRole(adminRole);
            userRepository.save(user);
            System.out.println(">> Test admin 'nisa' eklendi");
        } else {
            System.out.println(">> Admin 'nisa' zaten mevcut");
        }

        // Normal kullanıcı oluştur (zeynep)
        Role userRole = roleRepository.findByName(Role.RoleName.USER)
                .orElseGet(() -> roleRepository.save(new Role(Role.RoleName.USER)));
        Optional<User> existingZeynep = userRepository.findByUsername("zeynep");
        if (existingZeynep.isEmpty()) {
            User z = new User();
            z.setUsername("zeynep");
            z.setSifre(passwordEncoder.encode("1234"));
            z.setEnabled(true);
            z.setRole(userRole);
            userRepository.save(z);
            System.out.println(">> Test user 'zeynep' eklendi");
        } else {
            System.out.println(">> User 'zeynep' zaten mevcut");
        }

        // Var olan kullanıcıları Calisanlar ile eşleştir (username üzerinden)
        userRepository.findAll().forEach(u -> {
            // Önce bu user zaten bir çalışana bağlanmış mı?
            Optional<Calisanlar> mevcut = calisanlarRepository.findByUserId(u.getId());
            if (mevcut.isPresent()) {
                Calisanlar c = mevcut.get();
                if (c.getRole() == null)
                    c.setRole(u.getRole());
                if (c.getAd() == null || c.getAd().isBlank())
                    c.setAd(u.getUsername());
                calisanlarRepository.save(c);
                return;
            }

            // Ad üzerinden var mı?
            Optional<Calisanlar> adIle = calisanlarRepository.findByAd(u.getUsername());
            if (adIle.isPresent()) {
                Calisanlar c = adIle.get();
                if (c.getUser() == null)
                    c.setUser(u);
                if (c.getRole() == null)
                    c.setRole(u.getRole());
                calisanlarRepository.save(c);
                return;
            }

            // Yoksa yeni oluştur
            Calisanlar yeni = new Calisanlar();
            yeni.setAd(u.getUsername());
            yeni.setSoyad("");
            yeni.setEposta(u.getUsername() + "@local");
            yeni.setPozisyon("");
            yeni.setRole(u.getRole());
            yeni.setUser(u);
            calisanlarRepository.save(yeni);
        });
    }
}