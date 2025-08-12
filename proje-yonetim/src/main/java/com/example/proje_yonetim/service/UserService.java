package com.example.proje_yonetim.service;

//import java.util.Collections;
//import java.util.Set;
import org.springframework.security.crypto.password.PasswordEncoder;
//rt org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.proje_yonetim.entity.User;
import com.example.proje_yonetim.entity.Role;
import com.example.proje_yonetim.repository.RoleRepository;
import com.example.proje_yonetim.repository.UserRepository;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;

@Service
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User saveUser(User user) {
        // Kullanıcı zaten var mı kontrol et

        if (userRepository.findByUseradi(user.getUseradi()).isPresent()) {
            throw new RuntimeException("Bu kullanıcı adı zaten kullanılıyor: " + user.getUseradi());
        }

        // Şifreyi encode et
        user.setSifre(passwordEncoder.encode(user.getSifre()));

        // Varsayılan rol ekle (eğer rol yoksa)
        if (user.getRoles().isEmpty()) {
            Optional<Role> roleOptional = roleRepository.findByName("USER");
            Role defaultRole;
            if (roleOptional.isPresent()) {
                defaultRole = roleOptional.get();
            } else {
                defaultRole = new Role("USER");
                defaultRole = roleRepository.save(defaultRole);
            }
            user.addRole(defaultRole);
        }
        return userRepository.save(user);
    }

    public User registerUser(User user) {
        return saveUser(user);
    }

    public Optional<User> findByUseradi(String useradi) {
        return userRepository.findByUseradi(useradi);
    }

    public boolean existsByUseradi(String useradi) {
        return userRepository.findByUseradi(useradi).isPresent();
    }

    public User loginUser(String useradi, String sifre) {
        Optional<User> userOptional = userRepository.findByUseradi(useradi);

        if (userOptional.isPresent()) {
            User user = userOptional.get();
            if (passwordEncoder.matches(sifre, user.getSifre()) && user.isEnabled()) {
                return user;
            }
        }

        throw new RuntimeException("Geçersiz kullanıcı adı veya şifre");
    }
}
