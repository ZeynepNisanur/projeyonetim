package com.example.proje_yonetim.service;

import java.util.Collections;
import java.util.Set;
//import java.util.stream.Collectors;

//import org.springframework.security.core.authority.SimpleGrantedAuthority;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.security.core.userdetails.UserDetailsService;
//import org.springframework.security.core.userdetails.UsernameNotFoundException;
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

    public void registerUser(User user) {
        user.setSifre(passwordEncoder.encode(user.getSifre()));

        // Default rol ata (örnek: ROLE_USER)
        Role role = roleRepository.findByName("ROLE_USER");
        user.setRoles(Collections.singleton(role));

        userRepository.save(user);
    }

}
