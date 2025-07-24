package com.example.proje_yonetim.controller;

import com.example.proje_yonetim.entity.Projeler;
import com.example.proje_yonetim.service.ProjelerService;
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

    @PostMapping
    public Projeler projeEkle(@RequestBody Projeler projeler) {
        return projelerService.projeEkle(projeler);
    }

    @DeleteMapping("/{id}")
    public void projeSil(@PathVariable Long id) {
        projelerService.projeSil(id);
    }

    @GetMapping("/{id}")
    public Projeler projeGetir(@PathVariable Long id) {
        return projelerService.projeGetir(id).orElse(null);
    }
}
