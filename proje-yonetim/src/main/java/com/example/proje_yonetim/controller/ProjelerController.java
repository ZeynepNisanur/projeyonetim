package com.example.proje_yonetim.controller;

import com.example.proje_yonetim.dto.ProjelerDto;
import com.example.proje_yonetim.dto.ProjelerDurumGuncelleme;
import com.example.proje_yonetim.entity.Projeler;
import com.example.proje_yonetim.service.ProjelerService;

import org.springframework.security.core.Authentication;
import com.example.proje_yonetim.entity.Durum;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
// removed unused imports
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/projeler")
public class ProjelerController {

    private final ProjelerService projelerService;

    // removed unused field

    public ProjelerController(ProjelerService projelerService) {
        this.projelerService = projelerService;
    }

    @GetMapping
    public List<Projeler> tumProjeleriGetir() {
        return projelerService.tumProjeleriGetir();
    }

    @GetMapping("/{id}/detay")
    public ResponseEntity<ProjelerDto> getProje(@PathVariable Long id) {
        return ResponseEntity.ok(projelerService.getProje(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Projeler projeEkle(@RequestBody Projeler projeler) {
        return projelerService.projeEkle(projeler);
    }

    @PostMapping("/{projeId}/calisanlar/{calisanId}/ekle")
    public ResponseEntity<String> calisanEkle(@PathVariable Long projeId, @PathVariable Long calisanId) {
        projelerService.calisanEkle(projeId, calisanId);
        return ResponseEntity.ok("Çalışan projeye eklendi.");
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public void projeSil(@PathVariable Long id) {
        projelerService.projeSil(id);
    }

    @DeleteMapping("/{projeId}/calisanlar/{calisanId}/cikar")
    public ResponseEntity<String> calisanCikar(@PathVariable Long projeId, @PathVariable Long calisanId) {
        projelerService.calisanCikar(projeId, calisanId);
        return ResponseEntity.ok("Çalışan projeden çıkarıldı.");
    }

    @GetMapping("/{id}")
    public Projeler projeGetir(@PathVariable Long id) {
        return projelerService.projeGetir(id).orElse(null);
    }

    // ✅ Durum güncelleme (DTO kullanıyor)
    @PutMapping("/{projeId}/durum")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateProjectStatus(
            @PathVariable Long projeId,
            @RequestBody ProjelerDurumGuncelleme request) {

        try {
            projelerService.projeDurumGuncelle(projeId, request.getDurum());
            return ResponseEntity.ok().body(Map.of("message", "Durum güncellendi"));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", e.getMessage()));
        }
    }

    // ✅ Alternatif patch endpoint
    @PatchMapping("/{projeId}/durum")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateProjectPartial(
            @PathVariable Long projeId,
            @RequestBody Map<String, Object> updates,
            Authentication authentication) {

        try {
            if (updates.containsKey("durum")) {
                String durumStr = (String) updates.get("durum");
                Durum yeniDurum = Durum.valueOf(durumStr.toUpperCase());
                projelerService.projeDurumGuncelle(projeId, yeniDurum);
            }

            return ResponseEntity.ok(Map.of("message", "Durum güncellendi"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", "Geçersiz durum değeri"));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", e.getMessage()));
        }
    }
}