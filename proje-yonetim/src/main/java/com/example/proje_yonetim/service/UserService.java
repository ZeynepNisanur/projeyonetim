package com.example.proje_yonetim.service;

import java.util.Set;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.proje_yonetim.entity.User;
import com.example.proje_yonetim.entity.Role;
//import com.example.proje_yonetim.config.SecurityConfig;
import com.example.proje_yonetim.repository.RoleRepository;
import com.example.proje_yonetim.repository.UserRepository;

//import lombok.RequiredArgsConstructor;

@Service
// @RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // parametreleri alarak bir kullanıcı kaydı oluşturma:
    public void saveUserWithRole(String useradi, String sifre, String name) {
        Role role = roleRepository.findByName(name);
        if (role == null) {
            role = new Role();
            role.setName(name);
            role = roleRepository.save(role);
        }

        User user = new User();
        user.setUseradi(useradi);
        user.setSifre(passwordEncoder.encode(sifre)); // Şimdilik düz, sonra BCrypt ile değiştireceğiz.
        user.setRoles(Set.of(role));

        userRepository.save(user);
    }

}
