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

    public Optional<RefreshToken> getByUser(User user) {
        return refreshTokenRepository.findByUser(user);
    }

    public RefreshToken getOrCreateRefreshToken(String username) {
        User user = userRepository.findByUseradi(username);

        Optional<RefreshToken> existingToken = refreshTokenRepository.findByUser(user);

        if (existingToken.isPresent() && !isTokenExpired(existingToken.get())) {
            return existingToken.get(); // Var ve geçerli tokenı döndür
        }

        // Yoksa ya da süresi dolmuşsa yeni token oluştur
        if (existingToken.isPresent()) {
            refreshTokenRepository.delete(existingToken.get()); // Eskiyi sil
        }

        RefreshToken newToken = new RefreshToken();
        newToken.setUser(user);
        newToken.setToken(UUID.randomUUID().toString());
        newToken.setExpiryDate(LocalDateTime.now().plusSeconds(refreshTokenDurationMs / 1000));

        return refreshTokenRepository.save(newToken);
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

    // süresi geçmiş tokenı kontrol edip silme
    public RefreshToken getValidRefreshToken(String token) {
        RefreshToken refreshToken = findByToken(token)
                .orElseThrow(() -> new RuntimeException("Refresh token bulunamadı: " + token));

        if (isTokenExpired(refreshToken)) {
            refreshTokenRepository.delete(refreshToken);
            throw new RuntimeException("Refresh token süresi dolmuş. Lütfen tekrar giriş yapın.");
        }

        return refreshToken;
    }
}