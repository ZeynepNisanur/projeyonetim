package com.example.proje_yonetim.service;

import com.example.proje_yonetim.entity.Role;
import com.example.proje_yonetim.entity.User;
import com.example.proje_yonetim.repository.RoleRepository;
import com.example.proje_yonetim.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    // Varsayılan USER rolü ile kullanıcı oluşturma
    public User createUser(String username, String password, String email) {
        Role userRole = roleRepository.findByName(Role.RoleName.USER)
                .orElseThrow(() -> new RuntimeException("USER role not found"));

        User user = new User(username, password, email, userRole);
        return userRepository.save(user);
    }

    // Belirli bir rolle kullanıcı oluşturma
    public User createUserWithRole(String username, String password, String email, Role.RoleName roleName) {
        Role role = roleRepository.findByName(roleName)
                .orElseThrow(() -> new RuntimeException(roleName + " role not found"));

        User user = new User(username, password, email, role);
        return userRepository.save(user);
    }

    // Kullanıcının rolünü değiştirme
    public User changeUserRole(Long userId, Role.RoleName newRoleName) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Role newRole = roleRepository.findByName(newRoleName)
                .orElseThrow(() -> new RuntimeException(newRoleName + " role not found"));

        user.setRole(newRole);
        return userRepository.save(user);
    }

    // Admin kullanıcıları listeleme
    public List<User> getAllAdmins() {
        Role adminRole = roleRepository.findByName(Role.RoleName.ADMIN)
                .orElseThrow(() -> new RuntimeException("ADMIN role not found"));

        return userRepository.findByRole(adminRole);
    }

    // Normal kullanıcıları listeleme
    public List<User> getAllUsers() {
        Role userRole = roleRepository.findByName(Role.RoleName.USER)
                .orElseThrow(() -> new RuntimeException("USER role not found"));

        return userRepository.findByRole(userRole);
    }

    // Kullanıcının admin olup olmadığını kontrol etme
    public boolean isUserAdmin(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return user.isAdmin();
    }

    // Kullanıcıyı admin yapma
    public User promoteToAdmin(Long userId) {
        return changeUserRole(userId, Role.RoleName.ADMIN);
    }

    // Admin'i normal kullanıcı yapma
    public User demoteToUser(Long userId) {
        return changeUserRole(userId, Role.RoleName.USER);
    }

    /**
     * Kullanıcı adına göre kullanıcı bulma
     */
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    // Static method kaldırıldı, instance method kullanılmalı

    /**
     * Email'e göre kullanıcı bulma
     */
    public Optional<User> findByEposta(String eposta) {
        return userRepository.findByEposta(eposta);
    }

    /**
     * ID'ye göre kullanıcı bulma
     */
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    /**
     * Tüm kullanıcıları getir
     */
    public List<User> findAll() {
        return userRepository.findAll();
    }

    /**
     * Kullanıcı kaydet
     */
    public User save(User user) {
        return userRepository.save(user);
    }

    /**
     * Kullanıcı sil
     */
    public void deleteById(Long id) {
        userRepository.deleteById(id);
    }
}
