package com.example.proje_yonetim.controller;

import com.example.proje_yonetim.entity.Calisanlar;
import com.example.proje_yonetim.service.CalisanlarService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/calisanlar")
public class CalisanlarController {

    private final CalisanlarService calisanlarService;

    public CalisanlarController(CalisanlarService calisanlarService) {
        this.calisanlarService = calisanlarService;
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
}
