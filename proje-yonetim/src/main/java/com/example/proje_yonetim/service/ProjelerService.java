package com.example.proje_yonetim.service;

import com.example.proje_yonetim.entity.Calisanlar;
//import com.example.proje_yonetim.entity.Calisanlar;
import com.example.proje_yonetim.entity.Projeler;
import com.example.proje_yonetim.repository.CalisanlarRepository;
import com.example.proje_yonetim.repository.ProjelerRepository;

//import jakarta.persistence.JoinColumn;
//import jakarta.persistence.JoinTable;
//import jakarta.persistence.ManyToMany;

import org.springframework.stereotype.Service;

//import java.util.HashSet;
//import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class ProjelerService {
    private final ProjelerRepository projelerRepository;
    private final CalisanlarRepository calisanlarRepository;

    public ProjelerService(ProjelerRepository projelerRepository, CalisanlarRepository calisanlarRepository) {
        this.projelerRepository = projelerRepository;
        this.calisanlarRepository = calisanlarRepository;
    }

    public List<Projeler> tumProjeleriGetir() {
        return projelerRepository.findAll();
    }

    public List<Projeler> findAll(Projeler durum) {
        if (durum != null) {
            return projelerRepository.findByDurum(durum);
        }
        return projelerRepository.findAll();
    }

    public Optional<Projeler> projeGetir(Long id) {
        return projelerRepository.findById(id);
    }

    public Projeler projeEkle(Projeler projeler) {
        return projelerRepository.save(projeler);
        // varsa mevcut günceller
    }

    public void projeSil(Long id) {
        projelerRepository.deleteById(id);
    }

    public void calisanEkle(Long projeId, Long calisanId) {
        Projeler proje = projelerRepository.findById(projeId)
                .orElseThrow(() -> new RuntimeException("Proje bulunamadı"));

        Calisanlar calisan = calisanlarRepository.findById(calisanId)
                .orElseThrow(() -> new RuntimeException("Çalışan bulunamadı"));

        proje.getCalisanlar().add(calisan);
        projelerRepository.save(proje);
    }

}