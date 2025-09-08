package com.example.proje_yonetim.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.proje_yonetim.entity.Calisanlar;
import com.example.proje_yonetim.entity.Projeler;
import com.example.proje_yonetim.entity.Durum;
import com.example.proje_yonetim.service.CalisanlarService;
import com.example.proje_yonetim.service.ProjelerService;
import com.example.proje_yonetim.entity.User;
import com.example.proje_yonetim.repository.UserRepository;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    @Autowired
    private CalisanlarService calisanlarService;

    @Autowired
    private ProjelerService projelerService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private com.example.proje_yonetim.service.UserService userService;

    @GetMapping
    public ResponseEntity<?> getDashboard(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Kullanıcı doğrulanmadı");
        }

        Map<String, Object> data = new HashMap<>();
        String useradi = authentication.getName();

        boolean isAdmin = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(role -> role.equals("ROLE_ADMIN"));

        try {
            if (isAdmin) {
                // Admin için tüm veriler
                data.put("message", "Admin paneline hoş geldiniz, " + " " + useradi + " " + "!");
                data.put("userType", "ADMIN");

                // Temel istatistikler
                Map<String, Object> stats = new HashMap<>();
                List<Calisanlar> tumCalisanlar = calisanlarService.getAllCalisanlar();
                List<Projeler> tumProjeler = projelerService.tumProjeleriGetir();

                stats.put("toplamCalisanSayisi", tumCalisanlar.size());
                stats.put("toplamProjeSayisi", tumProjeler.size());

                // Proje durum dağılımı hesapla
                long devamEdenProjeler = tumProjeler.stream()
                        .filter(p -> Durum.DEVAM_EDIYOR.equals(p.getDurum()))
                        .count();
                long tamamlananProjeler = tumProjeler.stream()
                        .filter(p -> Durum.TAMAMLANDI.equals(p.getDurum()))
                        .count();
                long araverilenProjeler = tumProjeler.stream()
                        .filter(p -> Durum.ARA_VERILDI.equals(p.getDurum()))
                        .count();

                stats.put("devamEdenProjeler", devamEdenProjeler);
                stats.put("tamamlananProjeler", tamamlananProjeler);
                stats.put("araverilenProjeler", araverilenProjeler);

                data.put("statistics", stats);

                // Son eklenen projeler (son 5)
                List<Projeler> sonProjeler = tumProjeler.stream()
                        .sorted((p1, p2) -> Long.compare(p2.getId(), p1.getId())) // ID'ye göre azalan sırada
                        .limit(5)
                        .collect(Collectors.toList());
                data.put("sonProjeler", sonProjeler);

                // // Son eklenen çalışanlar (son 5)
                // List<Calisanlar> sonCalisanlar = tumCalisanlar.stream()
                // .sorted((c1, c2) -> Long.compare(c2.getId(), c1.getId())) // ID'ye göre
                // azalan sırada
                // .limit(5)
                // .collect(Collectors.toList());
                // data.put("sonCalisanlar", sonCalisanlar);

                // Proje durumu dağılımı
                Map<String, Object> durumDagilimi = new HashMap<>();
                durumDagilimi.put("DEVAM_EDIYOR", devamEdenProjeler);
                durumDagilimi.put("TAMAMLANDI", tamamlananProjeler);
                durumDagilimi.put("ARA_VERILDI", araverilenProjeler);
                data.put("projeDurumDagilimi", durumDagilimi);

                // Çalışan proje yoğunluk analizi
                Map<String, Object> calisanYogunluk = new HashMap<>();
                for (Calisanlar calisan : tumCalisanlar) {
                    long projeSayisi = tumProjeler.stream()
                            .filter(p -> p.getCalisanlar() != null && p.getCalisanlar().contains(calisan))
                            .count();
                    calisanYogunluk.put(calisan.getAd(), projeSayisi);
                }
                data.put("calisanYogunluk", calisanYogunluk);

            } else {
                // Normal kullanıcı için kendi verileri
                data.put("message", useradi + " için özel dashboard!");
                data.put("userType", "USER");

                // Kullanıcının kendi projelerini al (eposta ile eşleştirme yapılabilir)
                // Kullanıcının adını User.useradi üzerinden eşleştir
                User currentUser = userRepository.findByUsername(useradi).orElse(null);
                String currentAd = currentUser != null ? currentUser.getUsername() : null;

                List<Projeler> kullaniciProjeler = projelerService.tumProjeleriGetir().stream()
                        .filter(p -> p.getCalisanlar() != null && currentAd != null &&
                                p.getCalisanlar().stream().anyMatch(c -> currentAd.equals(c.getAd())))
                        .collect(Collectors.toList());

                data.put("benimProjelerim", kullaniciProjeler);

                // Kullanıcının istatistikleri
                Map<String, Object> userStats = new HashMap<>();
                userStats.put("projeSayisi", kullaniciProjeler.size());

                long aktifProjeler = kullaniciProjeler.stream()
                        .filter(p -> Durum.DEVAM_EDIYOR.equals(p.getDurum()))
                        .count();
                userStats.put("aktifProjeler", aktifProjeler);

                long tamamlananProjeler = kullaniciProjeler.stream()
                        .filter(p -> Durum.TAMAMLANDI.equals(p.getDurum()))
                        .count();
                userStats.put("tamamlananProjeler", tamamlananProjeler);

                data.put("statistics", userStats);
            }

            // Null-safe zorunlu alanlar
            data.putIfAbsent("statistics", new HashMap<>());
            data.putIfAbsent("projeDurumDagilimi", new HashMap<>());
            data.putIfAbsent("sonProjeler", List.of());
            data.putIfAbsent("benimProjelerim", List.of());

            data.put("username", useradi);
            data.put("timestamp", System.currentTimeMillis());

        } catch (Exception e) {
            data.put("error", "Veri yüklenirken hata oluştu: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(data);
        }

        return ResponseEntity.ok(data);
    }

    // Çalışanlar özet bilgisi endpoint'i (sadece admin)
    @GetMapping("/calisanlar-ozet")
    public ResponseEntity<?> getCalisanlarOzet(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Yetkisiz erişim");
        }

        boolean isAdmin = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(role -> role.equals("ROLE_ADMIN"));

        if (!isAdmin) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Bu işlem için admin yetkisi gerekli");
        }

        try {
            List<Calisanlar> calisanlar = calisanlarService.getAllCalisanlar();
            List<Projeler> projeler = projelerService.tumProjeleriGetir();

            Map<String, Object> ozet = new HashMap<>();
            ozet.put("toplamCalisanSayisi", calisanlar != null ? calisanlar.size() : 0);
            ozet.put("calisanlar", calisanlar != null ? calisanlar : List.of());

            Map<Long, Integer> calisanProjeSayilari = new HashMap<>();
            if (calisanlar != null && projeler != null) {
                for (Calisanlar calisan : calisanlar) {
                    int projeSayisi = (int) projeler.stream()
                            .filter(p -> p.getCalisanlar() != null && p.getCalisanlar().contains(calisan))
                            .count();
                    if (calisan.getId() != null) {
                        calisanProjeSayilari.put(calisan.getId(), projeSayisi);
                    }
                }
            }
            ozet.put("calisanProjeSayilari", calisanProjeSayilari);

            return ResponseEntity.ok(ozet);
        } catch (Exception e) {
            Map<String, Object> ozet = new HashMap<>();
            ozet.put("toplamCalisanSayisi", 0);
            ozet.put("calisanlar", List.of());
            ozet.put("calisanProjeSayilari", new HashMap<>());
            return ResponseEntity.ok(ozet);
        }
    }

    // Projeler özet bilgisi endpoint'i
    @GetMapping("/projeler-ozet")
    public ResponseEntity<?> getProjelerOzet(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Yetkisiz erişim");
        }

        String useradi = authentication.getName();
        boolean isAdmin = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(role -> role.equals("ROLE_ADMIN"));

        try {
            List<Projeler> projeler;
            if (isAdmin) {
                projeler = projelerService.tumProjeleriGetir();
            } else {
                User currentUser = userRepository.findByUsername(useradi).orElse(null);
                String currentAd = currentUser != null ? currentUser.getUsername() : null;
                projeler = projelerService.tumProjeleriGetir().stream()
                        .filter(p -> p.getCalisanlar() != null && currentAd != null &&
                                p.getCalisanlar().stream().anyMatch(c -> currentAd.equals(c.getAd())))
                        .collect(Collectors.toList());
            }

            Map<String, Object> ozet = new HashMap<>();
            ozet.put("toplamProjeSayisi", projeler != null ? projeler.size() : 0);
            ozet.put("projeler", projeler != null ? projeler : List.of());

            Map<String, Long> durumDagilimi = new HashMap<>();
            if (projeler != null) {
                for (Projeler proje : projeler) {
                    String durumKey = proje.getDurum() != null ? proje.getDurum().toString() : "BELIRTILMEMIS";
                    durumDagilimi.merge(durumKey, 1L, Long::sum);
                }
            }
            ozet.put("durumDagilimi", durumDagilimi);

            return ResponseEntity.ok(ozet);
        } catch (Exception e) {
            Map<String, Object> ozet = new HashMap<>();
            ozet.put("toplamProjeSayisi", 0);
            ozet.put("projeler", List.of());
            ozet.put("durumDagilimi", new HashMap<>());
            return ResponseEntity.ok(ozet);
        }
    }

    // Proje detayları endpoint'i
    @GetMapping("/proje-detay/{projeId}")
    public ResponseEntity<?> getProjeDetay(@PathVariable Long projeId, Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Yetkisiz erişim");
        }

        String useradi = authentication.getName();
        boolean isAdmin = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(role -> role.equals("ROLE_ADMIN"));

        try {
            // Projeyi getir
            Projeler proje = projelerService.projeGetir(projeId).orElse(null);
            if (proje == null) {
                return ResponseEntity.notFound().build();
            }

            // Admin değilse, kullanıcının projeye erişim yetkisi var mı kontrol et
            if (!isAdmin) {
                User currentUser = userRepository.findByUsername(useradi).orElse(null);
                String currentAd = currentUser != null ? currentUser.getUsername() : null;
                boolean kullaniciProjesinde = proje.getCalisanlar() != null && currentAd != null &&
                        proje.getCalisanlar().stream().anyMatch(c -> currentAd.equals(c.getAd()));

                if (!kullaniciProjesinde) {
                    return ResponseEntity.status(HttpStatus.FORBIDDEN)
                            .body("Bu projeye erişim yetkiniz yok");
                }
            }

            // Proje detaylarını hazırla
            Map<String, Object> detay = new HashMap<>();
            detay.put("proje", proje);
            detay.put("calisanSayisi", proje.getCalisanlar() != null ? proje.getCalisanlar().size() : 0);
            detay.put("durum", proje.getDurum());

            return ResponseEntity.ok(detay);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Proje detayları yüklenirken hata: " + e.getMessage());
        }
    }

    // Dashboard için hızlı istatistikler
    @GetMapping("/hizli-istatistikler")
    public ResponseEntity<?> getHizliIstatistikler(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Yetkisiz erişim");
        }

        boolean isAdmin = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(role -> role.equals("ROLE_ADMIN"));

        if (!isAdmin) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Bu işlem için admin yetkisi gerekli");
        }

        try {
            List<Calisanlar> calisanlar = calisanlarService.getAllCalisanlar();
            List<Projeler> projeler = projelerService.tumProjeleriGetir();

            Map<String, Object> istatistikler = new HashMap<>();

            // Temel sayılar
            istatistikler.put("toplamCalisanSayisi", calisanlar.size());
            istatistikler.put("toplamProjeSayisi", projeler.size());

            // Proje durumları
            Map<String, Long> durumlar = new HashMap<>();
            for (Projeler proje : projeler) {
                String durumKey = proje.getDurum() != null ? proje.getDurum().toString() : "BELIRTILMEMIS";
                durumlar.merge(durumKey, 1L, Long::sum);
            }
            istatistikler.put("projeDurumlari", durumlar);

            // Çalışan başına ortalama proje sayısı
            if (!calisanlar.isEmpty()) {
                double ortalamaProje = (double) projeler.size() / calisanlar.size();
                istatistikler.put("calisanBasinaOrtalamaProje", Math.round(ortalamaProje * 100.0) / 100.0);
            }

            return ResponseEntity.ok(istatistikler);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("İstatistikler yüklenirken hata: " + e.getMessage());
        }
    }

    @GetMapping("/user-projeler")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<?> getUserProjelerOzet(Authentication authentication) {
        try {
            String username = authentication.getName();
            System.out.println("User projeler özeti istendi: " + username);

            // Kullanıcının çalışan kaydını bul
            Optional<User> userOpt = userService.findByUsername(username);
            if (!userOpt.isPresent()) {
                Map<String, Object> emptyResponse = new HashMap<>();
                emptyResponse.put("toplamProjeSayisi", 0);
                emptyResponse.put("projeler", new ArrayList<>());
                emptyResponse.put("durumDagilimi", new HashMap<>());
                return ResponseEntity.ok(emptyResponse);
            }

            User user = userOpt.get();
            Optional<Calisanlar> calisanOpt = calisanlarService.findByUserId(user.getId());

            if (!calisanOpt.isPresent()) {
                Map<String, Object> emptyResponse = new HashMap<>();
                emptyResponse.put("toplamProjeSayisi", 0);
                emptyResponse.put("projeler", new ArrayList<>());
                emptyResponse.put("durumDagilimi", new HashMap<>());
                emptyResponse.put("message", "Çalışan kaydı bulunamadı");
                return ResponseEntity.ok(emptyResponse);
            }

            Calisanlar calisan = calisanOpt.get();

            // Çalışanın projelerini getir
            List<Projeler> userProjects = new ArrayList<>();
            if (calisan.getProjeler() != null) {
                userProjects.addAll(calisan.getProjeler());
            }

            // Durum dağılımını hesapla
            Map<String, Integer> durumDagilimi = new HashMap<>();
            for (Projeler proje : userProjects) {
                String durum = "BELIRTILMEMIS";
                if (proje.getDurum() != null) {
                    durum = proje.getDurum().toString();
                }
                durumDagilimi.put(durum, durumDagilimi.getOrDefault(durum, 0) + 1);
            }

            // En son eklenen projeleri al (maksimum 5)
            List<Projeler> sonProjeler = userProjects.stream()
                    .sorted((p1, p2) -> {
                        // ID'ye göre sırala (yeni ID'ler daha büyük olacak)
                        return Long.compare(p2.getId(), p1.getId());
                    })
                    .limit(5)
                    .collect(Collectors.toList());

            Map<String, Object> response = new HashMap<>();
            response.put("toplamProjeSayisi", userProjects.size());
            response.put("projeler", sonProjeler); // Sadece son 5 projeyi gönder
            response.put("durumDagilimi", durumDagilimi);
            response.put("calisanAdi", calisan.getAd() + " " + calisan.getSoyad());
            response.put("message", "Kullanıcı projeler özeti başarıyla getirildi");

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            System.err.println("User projeler özeti hatası: " + e.getMessage());
            e.printStackTrace();

            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("toplamProjeSayisi", 0);
            errorResponse.put("projeler", new ArrayList<>());
            errorResponse.put("durumDagilimi", new HashMap<>());
            errorResponse.put("error", "Projeler getirilemedi: " + e.getMessage());

            return ResponseEntity.ok(errorResponse);
        }
    }
}
