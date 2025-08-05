package com.example.proje_yonetim.service;

import com.example.proje_yonetim.entity.RefreshToken;
import com.example.proje_yonetim.entity.User;
import com.example.proje_yonetim.repository.RefreshTokenRepository;
import com.example.proje_yonetim.repository.UserRepository;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import jakarta.transaction.Transactional;

@Service
public class RefreshTokenService {

    @Value("${jwt.refreshExpirationMs}")
    private Long refreshTokenDurationMs;

    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;

    public RefreshTokenService(RefreshTokenRepository refreshTokenRepository, UserRepository userRepository) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.userRepository = userRepository;
    }

    public Optional<RefreshToken> findByToken(String token) {
        return refreshTokenRepository.findByToken(token);
    }

    public RefreshToken createRefreshToken(String username) {
        User user = userRepository.findByUseradi(username);

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUser(user);
        refreshToken.setToken(UUID.randomUUID().toString());
        refreshToken.setExpiryDate(LocalDateTime.now().plusSeconds(refreshTokenDurationMs / 1000));

        return refreshTokenRepository.save(refreshToken);
    }

    public boolean isTokenExpired(RefreshToken token) {
        return token.getExpiryDate().isBefore(LocalDateTime.now());
    }

    @Transactional
    public void deleteByUser(String username) {
        User user = userRepository.findByUseradi(username);
        refreshTokenRepository.deleteByUser(user);
    }
}