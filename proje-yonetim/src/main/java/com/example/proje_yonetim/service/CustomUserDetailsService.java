package com.example.proje_yonetim.service;

import com.example.proje_yonetim.entity.CustomUserDetails;
import com.example.proje_yonetim.entity.User;
import com.example.proje_yonetim.repository.UserRepository;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String useradi) throws UsernameNotFoundException {
        User user = userRepository.findByUseradi(useradi)
                .orElseThrow(() -> new UsernameNotFoundException("Kullanıcı bulunamadı: " + useradi));

        return new CustomUserDetails(user);

    }

}