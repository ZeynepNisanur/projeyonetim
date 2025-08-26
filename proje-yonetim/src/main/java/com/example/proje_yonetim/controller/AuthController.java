package com.example.proje_yonetim.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.proje_yonetim.dto.AuthRequestDto;
import com.example.proje_yonetim.dto.JwtResponse;
//import com.example.proje_yonetim.dto.TokenRefreshRequest;
//import com.example.proje_yonetim.entity.RefreshToken;
//import com.example.proje_yonetim.repository.UserRepository;
import com.example.proje_yonetim.security.JwtUtil;
import com.example.proje_yonetim.service.RefreshTokenService;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;
    // private final RefreshTokenService refreshTokenService;

    public AuthController(AuthenticationManager authenticationManager, JwtUtil jwtUtil,
            UserDetailsService userDetailsService, RefreshTokenService refreshTokenService) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
        // this.refreshTokenService = refreshTokenService;
    }

    @PostMapping("/login")
    @CrossOrigin(origins = "http://localhost:5173")
    public ResponseEntity<?> login(@RequestBody AuthRequestDto authRequest) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authRequest.getUseradi(),
                            authRequest.getSifre()));

            UserDetails userDetails = userDetailsService.loadUserByUsername(authRequest.getUseradi());
            String token = jwtUtil.generateToken(userDetails);

            return ResponseEntity.ok(new JwtResponse(token, null));

        } catch (Exception e) {
            return ResponseEntity.status(401).body("Geçersiz kullanıcı adı veya şifre");
        }

    }
}
