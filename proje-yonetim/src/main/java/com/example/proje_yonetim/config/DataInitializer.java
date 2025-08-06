package com.example.proje_yonetim.config;

import com.example.proje_yonetim.entity.Role;
import com.example.proje_yonetim.entity.User;
import com.example.proje_yonetim.repository.RoleRepository;
import com.example.proje_yonetim.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import java.util.Collections;

@Configuration
public class DataInitializer {

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserRepository userRepository;

    @PostConstruct
    public void init() {
        // Rol oluşturma
        Role adminRole = new Role();
        adminRole.setName("ROLE_ADMIN");
        roleRepository.save(adminRole);

        // Kullanıcı oluşturma
        User user = new User();
        user.setUseradi("nisa");
        user.setSifre("1234"); // Şimdilik şifreyi düz metin (gerçek uygulamada şifrelenmelii!)
        user.setEnabled(true);
        user.setRoles(Collections.singleton(adminRole));
        userRepository.save(user);

        System.out.println(">> Test kullanıcısı ve rol başarıyla eklendi");
    }
}