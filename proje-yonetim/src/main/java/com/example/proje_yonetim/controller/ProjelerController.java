package com.example.proje_yonetim.controller;

import com.example.proje_yonetim.dto.ProjelerDto;
import com.example.proje_yonetim.dto.ProjelerDurumGuncelleme;
import com.example.proje_yonetim.entity.Calisanlar;
import com.example.proje_yonetim.entity.Durum;
import com.example.proje_yonetim.entity.Projeler;
import com.example.proje_yonetim.entity.User;
import com.example.proje_yonetim.service.ProjelerService;
import com.example.proje_yonetim.service.UserService;
import com.example.proje_yonetim.service.CalisanlarService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/projeler")
public class ProjelerController {

    private final ProjelerService projelerService;
    private final UserService userService;
    private final CalisanlarService calisanlarService;

    // Autowired yerine constructor injection kullanmak daha iyidir
    public ProjelerController(ProjelerService projelerService, UserService userService,
            CalisanlarService calisanlarService) {
        this.projelerService = projelerService;
        this.userService = userService;
        this.calisanlarService = calisanlarService;
    }

    // Tüm projeleri sadece ADMIN rolüne sahip olanlar görebilir
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> tumProjeleriGetir() {
        try {
            List<Projeler> projeler = projelerService.tumProjeleriGetir();
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", projeler);
            response.put("count", projeler.size());
            response.put("message", "Tüm projeler başarıyla getirildi");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Projeler getirilemedi: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    // Proje detayını ADMIN veya o projeye atanmış bir USER görebilir
    @GetMapping("/{id}/detay")
    @PreAuthorize("hasRole('ADMIN') or @projelerService.isUserAssignedToProject(#id, authentication.name)")
    public ResponseEntity<?> getProjeDetay(@PathVariable Long id, Authentication authentication) {
        try {
            ProjelerDto projeDto = projelerService.getProje(id);
            if (projeDto != null) {
                return ResponseEntity.ok(projeDto);
            }
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Proje detayı alınırken hata: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    // Proje ekleme sadece ADMIN'e özel
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> projeEkle(@RequestBody Projeler projeler) {
        try {
            Projeler savedProje = projelerService.projeEkle(projeler);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", savedProje);
            response.put("message", "Proje başarıyla eklendi");
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Proje eklenirken hata: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }

    // Çalışan ekleme/çıkarma sadece ADMIN'e özel
    @PostMapping("/{projeId}/calisanlar/{calisanId}/ekle")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> calisanEkle(@PathVariable Long projeId, @PathVariable Long calisanId) {
        projelerService.calisanEkle(projeId, calisanId);
        return ResponseEntity.ok("Çalışan projeye başarıyla eklendi.");
    }

    @DeleteMapping("/{projeId}/calisanlar/{calisanId}/cikar")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> calisanCikar(@PathVariable Long projeId, @PathVariable Long calisanId) {
        projelerService.calisanCikar(projeId, calisanId);
        return ResponseEntity.ok("Çalışan projeden başarıyla çıkarıldı.");
    }

    // Proje silme sadece ADMIN'e özel
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> projeSil(@PathVariable Long id) {
        projelerService.projeSil(id);
        return ResponseEntity.ok("Proje başarıyla silindi.");
    }

    // Proje güncelleme sadece ADMIN'e özel
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateProject(@PathVariable Long id, @RequestBody Projeler updatedProje) {
        try {
            Optional<Projeler> existingProje = projelerService.projeGetir(id);
            if (!existingProje.isPresent()) {
                return ResponseEntity.notFound().build();
            }

            Projeler proje = existingProje.get();
            // Basit null kontrolleri yerine daha iyi bir yaklaşım
            proje.setBaslik(updatedProje.getBaslik() != null ? updatedProje.getBaslik() : proje.getBaslik());
            proje.setAciklama(updatedProje.getAciklama() != null ? updatedProje.getAciklama() : proje.getAciklama());
            proje.setBaslangicTarihi(updatedProje.getBaslangicTarihi() != null ? updatedProje.getBaslangicTarihi()
                    : proje.getBaslangicTarihi());
            proje.setBitisTarihi(
                    updatedProje.getBitisTarihi() != null ? updatedProje.getBitisTarihi() : proje.getBitisTarihi());
            proje.setDurum(updatedProje.getDurum() != null ? updatedProje.getDurum() : proje.getDurum());
            proje.setCalisanlar(
                    updatedProje.getCalisanlar() != null ? updatedProje.getCalisanlar() : proje.getCalisanlar());

            Projeler savedProje = projelerService.projeEkle(proje);
            return ResponseEntity.ok(savedProje);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", "Proje güncellenirken hata: " + e.getMessage()));
        }
    }

    // Sadece ADMIN rolüne sahip olanlar bu endpoint'i kullanabilir.
    @PutMapping("/{projeId}/durum")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateProjectStatus(
            @PathVariable Long projeId,
            @RequestBody ProjelerDurumGuncelleme request) {
        try {
            projelerService.projeDurumGuncelle(projeId, request.getDurum());
            return ResponseEntity.ok().body(Map.of("message", "Durum başarıyla güncellendi"));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", "Durum güncellenirken hata: " + e.getMessage()));
        }
    }

    // Sadece ADMIN rolüne sahip olanlar bu endpoint'i kullanabilir.
    @PatchMapping("/{projeId}/durum")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateProjectPartial(
            @PathVariable Long projeId,
            @RequestBody Map<String, Object> updates) {
        try {
            if (updates.containsKey("durum")) {
                String durumStr = (String) updates.get("durum");
                Durum yeniDurum = Durum.valueOf(durumStr.toUpperCase());
                projelerService.projeDurumGuncelle(projeId, yeniDurum);
            }

            return ResponseEntity.ok(Map.of("message", "Durum başarıyla güncellendi"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", "Geçersiz durum değeri: " + e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", "Proje güncellenirken hata: " + e.getMessage()));
        }
    }

    // Kullanıcının kendi projelerini getir (hem USER hem ADMIN görebilir)
    @GetMapping("/my-projects")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> getMyProjects(Authentication authentication) {
        try {
            String username = authentication.getName();
            Optional<User> userOpt = userService.findByUsername(username);

            if (!userOpt.isPresent()) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("message", "Kullanıcı bulunamadı");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
            }
            User user = userOpt.get();

            Optional<Calisanlar> calisanOpt = calisanlarService.findByUserId(user.getId());
            if (!calisanOpt.isPresent()) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", true);
                response.put("data", new ArrayList<>());
                response.put("count", 0);
                response.put("message", "Bu kullanıcıya henüz çalışan kaydı atanmamış");
                return ResponseEntity.ok(response);
            }

            Calisanlar calisan = calisanOpt.get();
            List<Projeler> userProjects = projelerService.getProjectsByCalisan(calisan.getId());

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", userProjects);
            response.put("count", userProjects.size());
            response.put("message", "Kendi projeleriniz başarıyla getirildi");
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Projeler getirilemedi: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    // Dashboard için kullanıcı projelerini getir (hem USER hem ADMIN görebilir)
    @GetMapping("/user-projects")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> getUserProjectsForDashboard(Authentication authentication) {
        try {
            String username = authentication.getName();
            Optional<User> userOpt = userService.findByUsername(username);

            if (!userOpt.isPresent()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }

            User user = userOpt.get();
            Optional<Calisanlar> calisanOpt = calisanlarService.findByUserId(user.getId());

            if (!calisanOpt.isPresent()) {
                Map<String, Object> response = new HashMap<>();
                response.put("toplamProjeSayisi", 0);
                response.put("projeler", new ArrayList<>());
                response.put("durumDagilimi", new HashMap<>());
                return ResponseEntity.ok(response);
            }

            Calisanlar calisan = calisanOpt.get();
            List<Projeler> userProjects = projelerService.getProjectsByCalisan(calisan.getId());

            Map<String, Integer> durumDagilimi = new HashMap<>();
            for (Projeler proje : userProjects) {
                String durum = proje.getDurum() != null ? proje.getDurum().toString() : "BELIRTILMEMIS";
                durumDagilimi.put(durum, durumDagilimi.getOrDefault(durum, 0) + 1);
            }

            Map<String, Object> response = new HashMap<>();
            response.put("toplamProjeSayisi", userProjects.size());
            response.put("projeler", userProjects);
            response.put("durumDagilimi", durumDagilimi);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("toplamProjeSayisi", 0);
            errorResponse.put("projeler", new ArrayList<>());
            errorResponse.put("durumDagilimi", new HashMap<>());
            // Başarısız durumda 200 OK yerine 500 Internal Server Error dönmek daha
            // mantıklı
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
}