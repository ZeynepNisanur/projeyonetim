package com.example.proje_yonetim.controller;

//import org.apache.catalina.User;

//import java.time.LocalDateTime;
//import com.example.proje_yonetim.entity.User;
//import com.example.proje_yonetim.repository.UserRepository;
//import org.springframework.beans.factory.annotation.Autowired;

//import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.proje_yonetim.dto.AuthRequestDto;
//import com.example.proje_yonetim.dto.AuthResponse;
import com.example.proje_yonetim.dto.JwtResponse;
//import com.example.proje_yonetim.dto.TokenRefreshRequest;
//import com.example.proje_yonetim.entity.RefreshToken;
//import com.example.proje_yonetim.repository.UserRepository;
//import com.example.proje_yonetim.entity.RefreshToken;
import com.example.proje_yonetim.security.JwtUtil;
//import com.example.proje_yonetim.dto.AuthRequestDto;
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

    // GET ile HTML form göster localhosttan get isteği geldiği için
    @GetMapping("/login")
    public String showLoginForm() {
        return """
                    <html>
                        <body>
                            <h2>Giriş Yap</h2>
                            <form method="post" action="/login">
                                <input type="text" name="useradi" placeholder="Kullanıcı Adı" />
                                <br/>
                                <input type="password" name="sifre" placeholder="Şifre" />
                                <br/>
                                <button type="submit">Giriş</button>
                            </form>
                        </body>
                    </html>
                """;
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

            // Refresh token üret
            // String refreshToken =
            // refreshTokenService.createRefreshToken(userDetails.getUsername()).getToken();

            // Tokenları birlikte gönder
            return ResponseEntity.ok(new JwtResponse(token, null));

        } catch (Exception e) {
            return ResponseEntity.status(401).body("Geçersiz kullanıcı adı veya şifre");
        }

    }
}
