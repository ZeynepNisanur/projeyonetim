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

    @PostMapping("/login")
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

    /*
     * @Autowired
     * 
     * @PostMapping("/login")
     * public ResponseEntity<?> login(@RequestBody AuthRequestDto authRequest) {
     * try {
     * System.out.println(">>> Giriş denemesi: " + authRequest.getUseradi() + " / "
     * + authRequest.getSifre());
     * 
     * authenticationManager.authenticate(
     * new UsernamePasswordAuthenticationToken(authRequest.getUseradi(),
     * authRequest.getSifre()));
     * 
     * // User bilgilerini alalım
     * 
     * UserRepository userRepository = null;
     * User user = userRepository.findByUseradi(authRequest.getUseradi());
     * if (user != null) {
     * System.out.println(">>> DB'deki kullanıcı şifresi (hash): " +
     * user.getSifre());
     * System.out.println(">>> Kullanıcı enabled durumu: " + user.isEnabled());
     * System.out.println(">>> Kullanıcının rolleri: ");
     * user.getRoles().forEach(role -> System.out.println("- " + role.getName()));
     * }
     * 
     * UserDetails userDetails =
     * userDetailsService.loadUserByUsername(authRequest.getUseradi());
     * String token = jwtUtil.generateToken(userDetails);
     * 
     * // Refresh token üret
     * String refreshToken =
     * refreshTokenService.createRefreshToken(userDetails.getUsername()).getToken();
     * 
     * return ResponseEntity.ok(new JwtResponse(token, refreshToken));
     * 
     * } catch (Exception e) {
     * e.printStackTrace(); // Hata detayını görmek için
     * return ResponseEntity.status(401).body("Geçersiz kullanıcı adı veya şifre");
     * }
     * }
     */

    /*
     * @PostMapping("/refresh-token")
     * public ResponseEntity<?> refreshToken(@RequestBody TokenRefreshRequest
     * request) {
     * String requestToken = request.getRefreshToken();
     * 
     * return refreshTokenService.findByToken(requestToken)
     * .map(refreshToken -> {
     * if (refreshTokenService.isTokenExpired(refreshToken)) {
     * refreshTokenRepository.delete(refreshToken);
     * return ResponseEntity.status(HttpStatus.FORBIDDEN)
     * .body("token süresi dolmuştur. yeniden giriş yapın.");
     * }
     * String username = refreshToken.getUser().getUseradi();
     * UserDetails userDetails = userDetailsService.loadUserByUsername(username);
     * String newAccessToken = jwtUtil.generateToken(userDetails);
     * JwtResponse jwtResponse = new JwtResponse(newAccessToken, requestToken);
     * 
     * return ResponseEntity.ok(jwtResponse);
     * })
     * .orElseGet(() ->
     * ResponseEntity.badRequest().body("Refresh token bulunamadı"));
     * }
     */

    /*
     * @PostMapping("/refresh-token")
     * public ResponseEntity<?> refreshToken(@RequestBody TokenRefreshRequest
     * request) {
     * String requestToken = request.getRefreshToken();
     * 
     * try {
     * RefreshToken refreshToken =
     * refreshTokenService.getValidRefreshToken(requestToken); // burada hem
     * // kontrol,hem silme var
     * 
     * String username = refreshToken.getUser().getUseradi();
     * UserDetails userDetails = userDetailsService.loadUserByUsername(username);
     * String newAccessToken = jwtUtil.generateToken(userDetails);
     * JwtResponse jwtResponse = new JwtResponse(newAccessToken, requestToken);
     * 
     * return ResponseEntity.ok(jwtResponse);
     * } catch (RuntimeException ex) {
     * return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ex.getMessage());
     * }
     * }
     */

}
