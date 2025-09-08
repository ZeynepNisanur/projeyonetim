package com.example.proje_yonetim.controller;

import com.example.proje_yonetim.entity.Calisanlar;
import com.example.proje_yonetim.service.CalisanlarService;
import com.example.proje_yonetim.service.ProjelerService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/api/calisanlar")
public class CalisanlarController {

    private final CalisanlarService calisanlarService;
    private final ProjelerService projelerService;

    public CalisanlarController(CalisanlarService calisanlarService, ProjelerService projelerService) {
        this.calisanlarService = calisanlarService;
        this.projelerService = projelerService;
    }

    @GetMapping
    public ResponseEntity<?> getAllCalisanlar() {
        try {
            List<Calisanlar> calisanlar = calisanlarService.getAllCalisanlar();
            System.out.println("Controller: " + calisanlar.size() + " çalışan döndürülüyor"); // Debug

            // Response wrapper ile daha detaylı bilgi gönder
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", calisanlar);
            response.put("count", calisanlar.size());
            response.put("message", "Çalışanlar başarıyla getirildi");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.err.println("Çalışanları getirirken hata: " + e.getMessage());
            e.printStackTrace();

            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Çalışanlar getirilemedi: " + e.getMessage());

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getCalisanById(@PathVariable Long id) {
        try {
            Calisanlar calisan = calisanlarService.getCalisanById(id);
            if (calisan != null) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", true);
                response.put("data", calisan);
                return ResponseEntity.ok(response);
            } else {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "Çalışan bulunamadı");
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            System.err.println("Çalışan detayı getirirken hata: " + e.getMessage());
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Çalışan detayı alınamadı: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> createCalisan(@RequestBody Calisanlar calisan) {
        try {
            System.out.println("Yeni çalışan ekleniyor: " + calisan.getAd() + " " + calisan.getSoyad());
            Calisanlar savedCalisan = calisanlarService.createCalisan(calisan);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", savedCalisan);
            response.put("message", "Çalışan başarıyla eklendi");

            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            System.err.println("Çalışan eklerken hata: " + e.getMessage());
            e.printStackTrace();

            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Çalışan eklenemedi: " + e.getMessage());

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateCalisan(@PathVariable Long id, @RequestBody Calisanlar calisan) {
        try {
            Calisanlar guncellenen = calisanlarService.updateCalisan(id, calisan);
            if (guncellenen != null) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", true);
                response.put("data", guncellenen);
                response.put("message", "Çalışan başarıyla güncellendi");
                return ResponseEntity.ok(response);
            } else {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "Güncellenecek çalışan bulunamadı");
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            System.err.println("Çalışan güncellerken hata: " + e.getMessage());
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Çalışan güncellenemedi: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteCalisan(@PathVariable Long id) {
        try {
            calisanlarService.deleteCalisan(id);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Çalışan başarıyla silindi");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.err.println("Çalışan silerken hata: " + e.getMessage());
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Çalışan silinemedi: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    // Debug endpoint - çalışan sayısını kontrol et
    @GetMapping("/count")
    public ResponseEntity<?> getCalisanCount() {
        try {
            long count = calisanlarService.getCalisanSayisi();
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("count", count);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Çalışan sayısı alınamadı: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    // User ile çalışanı bağlama endpoint'i
    @PostMapping("/{calisanId}/link-user/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> linkCalisanToUser(
            @PathVariable Long calisanId,
            @PathVariable Long userId) {
        try {
            Calisanlar calisan = calisanlarService.linkCalisanToUser(calisanId, userId);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", calisan);
            response.put("message", "Çalışan User'a başarıyla bağlandı");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Bağlantı kurulamadı: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }

    @PostMapping("/{calisanId}/projeler/{projeId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> calisaniProjeEkle(
            @PathVariable Long calisanId,
            @PathVariable Long projeId) {
        try {
            projelerService.calisanEkle(projeId, calisanId);
            return ResponseEntity.ok("Çalışan projeye eklendi.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Çalışan projeye eklenemedi: " + e.getMessage());
        }
    }

    @DeleteMapping("/{calisanId}/projeler/{projeId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> calisaniProjedenCikar(
            @PathVariable Long calisanId,
            @PathVariable Long projeId) {
        try {
            projelerService.calisanCikar(projeId, calisanId);
            return ResponseEntity.ok("Çalışan projeden çıkarıldı.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Çalışan projeden çıkarılamadı: " + e.getMessage());
        }
    }
}