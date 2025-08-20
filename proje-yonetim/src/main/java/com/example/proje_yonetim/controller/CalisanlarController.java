package com.example.proje_yonetim.controller;

import com.example.proje_yonetim.entity.Calisanlar;
import com.example.proje_yonetim.service.CalisanlarService;
import com.example.proje_yonetim.service.ProjelerService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/calisanlar")
public class CalisanlarController {

    private final CalisanlarService calisanlarService;
    private final ProjelerService projelerService; // Doğru tanımlama

    // Constructor injection (önerilen yöntem)
    public CalisanlarController(CalisanlarService calisanlarService, ProjelerService projelerService) {
        this.calisanlarService = calisanlarService;
        this.projelerService = projelerService;
    }

    @GetMapping
    public ResponseEntity<List<Calisanlar>> getAllCalisanlar() {
        return ResponseEntity.ok(calisanlarService.getAllCalisanlar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Calisanlar> getCalisanById(@PathVariable Long id) {
        Calisanlar calisan = calisanlarService.getCalisanById(id);
        if (calisan != null) {
            return ResponseEntity.ok(calisan);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ResponseEntity<Calisanlar> createCalisan(@RequestBody Calisanlar calisan) {
        return ResponseEntity.ok(calisanlarService.createCalisan(calisan));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Calisanlar> updateCalisan(@PathVariable Long id, @RequestBody Calisanlar calisan) {
        Calisanlar guncellenen = calisanlarService.updateCalisan(id, calisan);
        if (guncellenen != null) {
            return ResponseEntity.ok(guncellenen);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCalisan(@PathVariable Long id) {
        calisanlarService.deleteCalisan(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{calisanId}/projeler/{projeId}")
    public ResponseEntity<String> calisaniProjeEkle(
            @PathVariable Long calisanId,
            @PathVariable Long projeId) {
        projelerService.calisanEkle(projeId, calisanId);
        return ResponseEntity.ok("Çalışan projeye eklendi.");
    }

    @DeleteMapping("/{calisanId}/projeler/{projeId}")
    public ResponseEntity<String> calisaniProjedenCikar(
            @PathVariable Long calisanId,
            @PathVariable Long projeId) {
        projelerService.calisanCikar(projeId, calisanId);
        return ResponseEntity.ok("Çalışan projeden çıkarıldı.");
    }
}