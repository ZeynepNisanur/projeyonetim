package com.example.proje_yonetim.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
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

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    @Autowired
    private CalisanlarService calisanlarService;

    @Autowired
    private ProjelerService projelerService;

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

                // Son eklenen çalışanlar (son 5)
                List<Calisanlar> sonCalisanlar = tumCalisanlar.stream()
                        .sorted((c1, c2) -> Long.compare(c2.getId(), c1.getId())) // ID'ye göre azalan sırada
                        .limit(5)
                        .collect(Collectors.toList());
                data.put("sonCalisanlar", sonCalisanlar);

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

                // Kullanıcının kendi projelerini al (email ile eşleştirme yapılabilir)
                List<Projeler> kullaniciProjeler = projelerService.tumProjeleriGetir().stream()
                        .filter(p -> {
                            // Burada kullanıcının projelerini filtreleme mantığını ekleyin
                            // Örnek: çalışan email'i ile eşleştirme
                            return p.getCalisanlar() != null &&
                                    p.getCalisanlar().stream()
                                            .anyMatch(c -> useradi.equals(c.getEposta()));
                        })
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
            ozet.put("toplamCalisanSayisi", calisanlar.size());
            ozet.put("calisanlar", calisanlar);

            // Her çalışan için proje sayısını hesapla
            Map<Long, Integer> calisanProjeSayilari = new HashMap<>();
            for (Calisanlar calisan : calisanlar) {
                int projeSayisi = (int) projeler.stream()
                        .filter(p -> p.getCalisanlar() != null && p.getCalisanlar().contains(calisan))
                        .count();
                calisanProjeSayilari.put(calisan.getId(), projeSayisi);
            }
            ozet.put("calisanProjeSayilari", calisanProjeSayilari);

            return ResponseEntity.ok(ozet);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Çalışanlar özeti yüklenirken hata: " + e.getMessage());
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
                // Admin tüm projeleri görebilir
                projeler = projelerService.tumProjeleriGetir();
            } else {
                // Normal kullanıcı sadece kendi projelerini görebilir
                projeler = projelerService.tumProjeleriGetir().stream()
                        .filter(p -> {
                            // Kullanıcının projelerini filtreleme mantığı
                            return p.getCalisanlar() != null &&
                                    p.getCalisanlar().stream()
                                            .anyMatch(c -> useradi.equals(c.getEposta()));
                        })
                        .collect(Collectors.toList());
            }

            Map<String, Object> ozet = new HashMap<>();
            ozet.put("toplamProjeSayisi", projeler.size());
            ozet.put("projeler", projeler);

            // Durum dağılımı
            Map<String, Long> durumDagilimi = new HashMap<>();
            for (Projeler proje : projeler) {
                String durumKey = proje.getDurum() != null ? proje.getDurum().toString() : "BELIRTILMEMIS";
                durumDagilimi.merge(durumKey, 1L, Long::sum);
            }
            ozet.put("durumDagilimi", durumDagilimi);

            return ResponseEntity.ok(ozet);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Projeler özeti yüklenirken hata: " + e.getMessage());
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
                boolean kullaniciProjesinde = proje.getCalisanlar() != null &&
                        proje.getCalisanlar().stream()
                                .anyMatch(c -> useradi.equals(c.getEposta()));

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
}

/*
 * boolean isUser = authentication.getAuthorities().stream()
 * .map(GrantedAuthority::getAuthority)
 * .anyMatch(role -> role.equals("ROLE_USER"));
 * 
 * if (isAdmin) {
 * data.put("message",
 * "Admin paneline hoş geldiniz!" + " " + useradi + " " + data.put("modules",
 * Arrays.asList(
 * "Çalışan Listesi ",
 * "Proje Yönetimi",
 * "Kullanıcı Yönetimi")));
 * data.put("userType", "ADMIN");
 * } else if (isUser) {
 * data.put("message", useradi + " için özel dashboard!");
 * data.put("modules", Arrays.asList(" Kendi Projelerim", "PRofil Ayarları"));
 * data.put("userType", "USER");
 * } else {
 * data.put("message", "Hoş geldiniz," + useradi);
 * data.put("modules", Arrays.asList("Genel Bilgiler"));
 * data.put("userType", "UNKNOWN");
 * }
 * data.put("username", useradi);
 * data.put("timestamp", System.currentTimeMillis());
 * data.put("roles", authentication.getAuthorities().stream()
 * .map(GrantedAuthority::getAuthority)
 * .toArray());
 * return ResponseEntity.ok(data);
 * }
 * 
 * @GetMapping("/user-info")
 * public ResponseEntity<?> getUserInfo(Authentication authentication) {
 * if (authentication == null || !authentication.isAuthenticated()) {
 * return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
 * .body("Kullanıcı doğrulanmadı");
 * }
 * 
 * Map<String, Object> userInfo = new HashMap<>();
 * userInfo.put("username", authentication.getName());
 * userInfo.put("roles", authentication.getAuthorities().stream()
 * .map(GrantedAuthority::getAuthority)
 * .toArray());
 * userInfo.put("authenticated", true);
 * 
 * return ResponseEntity.ok(userInfo);
 * }
 * }
 */
