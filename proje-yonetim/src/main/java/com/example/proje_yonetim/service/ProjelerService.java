package com.example.proje_yonetim.service;

import com.example.proje_yonetim.dto.CalisanlarDto;
import com.example.proje_yonetim.dto.ProjelerDto;
import com.example.proje_yonetim.entity.Calisanlar;
//import com.example.proje_yonetim.entity.Calisanlar;
import com.example.proje_yonetim.entity.Projeler;
import com.example.proje_yonetim.repository.CalisanlarRepository;
import com.example.proje_yonetim.repository.ProjelerRepository;

import org.springframework.beans.factory.annotation.Autowired;
import com.example.proje_yonetim.entity.Durum;
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

    public List<Projeler> findAll(Durum durum) {
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

    public void calisanlarEkle(Long projeId, Long calisanId) {
        Projeler proje = projelerRepository.findById(projeId)
                .orElseThrow(() -> new RuntimeException("Proje bulunamadı"));

        Calisanlar calisan = calisanlarRepository.findById(calisanId)
                .orElseThrow(() -> new RuntimeException("Çalışan bulunamadı"));

        proje.getCalisanlar().add(calisan);
        projelerRepository.save(proje);
    }

    @Autowired
    private ProjelerRepository projelerRepo;

    @Autowired
    private CalisanlarRepository calisanlarRepo;

    public ProjelerDto getProje(Long projeId) {
        Projeler projeler = projelerRepo.findById(projeId)
                .orElseThrow(() -> new RuntimeException("Proje bulunamadı"));

        List<CalisanlarDto> calisanlarDtos = projeler.getCalisanlar().stream()
                .map(c -> new CalisanlarDto(c.getId(), c.getAdSoyad()))
                .toList();

        return new ProjelerDto(projeler.getId(), projeler.getBaslik(), calisanlarDtos);
    }

    public void calisanEkle(Long projeId, Long calisanId) {
        Projeler projeler = projelerRepo.findById(projeId)
                .orElseThrow(() -> new RuntimeException("Proje bulunamadı"));

        Calisanlar calisanlar = calisanlarRepo.findById(calisanId)
                .orElseThrow(() -> new RuntimeException("Çalışan bulunamadı"));

        projeler.getCalisanlar().add(calisanlar);
        projelerRepo.save(projeler);
    }

    public void calisanCikar(Long projeId, Long calisanId) {
        Projeler projeler = projelerRepo.findById(projeId)
                .orElseThrow(() -> new RuntimeException("Proje bulunamadı"));

        Calisanlar calisanlar = calisanlarRepo.findById(calisanId)
                .orElseThrow(() -> new RuntimeException("Çalışan bulunamadı"));

        projeler.getCalisanlar().remove(calisanlar);
        projelerRepo.save(projeler);
    }

    public void projeDurumGuncelle(Long projeId, Durum yeniDurum) {
        Projeler proje = projelerRepo.findById(projeId)
                .orElseThrow(() -> new RuntimeException("Proje bulunamadı"));

        proje.setDurum(yeniDurum);
        projelerRepository.save(proje);
    }
}
