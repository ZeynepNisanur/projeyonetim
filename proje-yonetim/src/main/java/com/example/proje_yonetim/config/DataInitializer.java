package com.example.proje_yonetim.config;

import com.example.proje_yonetim.entity.Role;
import com.example.proje_yonetim.entity.User;
import com.example.proje_yonetim.repository.RoleRepository;
import com.example.proje_yonetim.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

@Configuration
public class DataInitializer {

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostConstruct
    public void init() {
        // ROLE_ADMIN rolünü oluştur (eğer yoksa)
        Role adminRole;
        Optional<Role> existingAdminRole = roleRepository.findByName("ROLE_ADMIN");
        if (existingAdminRole.isEmpty()) {
            adminRole = new Role();
            adminRole.setName("ROLE_ADMIN");
            adminRole = roleRepository.save(adminRole);
            System.out.println(">> ROLE_ADMIN rolü oluşturuldu");
        } else {
            adminRole = existingAdminRole.get();
            System.out.println(">> ROLE_ADMIN rolü zaten mevcut");
        }

        // USER rolünü oluştur (eğer yoksa)
        Optional<Role> existingUserRole = roleRepository.findByName("ROLE_USER");
        if (existingUserRole.isEmpty()) {
            Role userRole = new Role();
            userRole.setName("ROLE_USER");
            roleRepository.save(userRole);
            System.out.println(">> ROLE_USER rolü oluşturuldu");
        } else {
            System.out.println(">> ROLE_USER rolü zaten mevcut");
        }
        // Admin kullanıcısı oluştur (eğer yoksa)
        Optional<User> existingUser = userRepository.findByUseradi("nisa");
        if (existingUser.isEmpty()) {
            User user = new User();
            user.setUseradi("nisa");
            user.setSifre(passwordEncoder.encode("1234"));
            user.setEnabled(true);

            // Önce kullanıcıyı kaydet
            user = userRepository.save(user);

            // Sonra rolü ekle
            user.getRoles().add(adminRole);
            userRepository.save(user);

            System.out.println(">> Test kullanıcısı 'nisa' başarıyla eklendi");
        } else {
            System.out.println(">> Kullanıcı 'nisa' zaten mevcut");
        }
    }
}