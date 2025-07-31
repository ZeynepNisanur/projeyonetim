package com.example.proje_yonetim.controller;

import com.example.proje_yonetim.dto.ProjelerDto;
import com.example.proje_yonetim.dto.ProjelerDurumGuncelleme;
import com.example.proje_yonetim.entity.Projeler;
import com.example.proje_yonetim.service.ProjelerService;
//import com.example.proje_yonetim.entity.Durum; 
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/projeler")
public class ProjelerController {

    private final ProjelerService projelerService;

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
    public Projeler projeEkle(@RequestBody Projeler projeler) {
        return projelerService.projeEkle(projeler);
    }

    @PostMapping("/{projeId}/calisanlar/{calisanId}/ekle")
    public ResponseEntity<String> calisanEkle(@PathVariable Long projeId, @PathVariable Long calisanId) {
        projelerService.calisanEkle(projeId, calisanId);
        return ResponseEntity.ok("Çalışan projeye eklendi.");
    }

    @DeleteMapping("/{id}")
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

    @PutMapping("/{projeId}/durum")
    public ResponseEntity<String> projeDurumGuncelle(
            @PathVariable Long projeId,
            @RequestBody ProjelerDurumGuncelleme request) {

        projelerService.projeDurumGuncelle(projeId, request.getDurum());
        return ResponseEntity.ok("Durum güncellendi");
    }
}
