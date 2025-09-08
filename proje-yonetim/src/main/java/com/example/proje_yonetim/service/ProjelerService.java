package com.example.proje_yonetim.service;

import com.example.proje_yonetim.dto.CalisanlarDto;
import com.example.proje_yonetim.dto.ProjelerDto;
import com.example.proje_yonetim.entity.Calisanlar;
import com.example.proje_yonetim.entity.Projeler;
import com.example.proje_yonetim.repository.CalisanlarRepository;
import com.example.proje_yonetim.repository.ProjelerRepository;
import com.example.proje_yonetim.entity.Durum;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.HashMap;
import java.util.Map;

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
    }

    public void projeSil(Long id) {
        projelerRepository.deleteById(id);
    }

    public ProjelerDto getProje(Long projeId) {
        Projeler projeler = projelerRepository.findById(projeId)
                .orElseThrow(() -> new RuntimeException("Proje bulunamadı"));

        List<CalisanlarDto> calisanlarDtos = projeler.getCalisanlar().stream()
                .map(c -> new CalisanlarDto(c.getId(), c.getAdSoyad()))
                .collect(Collectors.toList());

        return new ProjelerDto(projeler.getId(), projeler.getBaslik(), calisanlarDtos);
    }

    public void calisanEkle(Long projeId, Long calisanId) {
        Projeler projeler = projelerRepository.findById(projeId)
                .orElseThrow(() -> new RuntimeException("Proje bulunamadı"));

        Calisanlar calisanlar = calisanlarRepository.findById(calisanId)
                .orElseThrow(() -> new RuntimeException("Çalışan bulunamadı"));

        projeler.getCalisanlar().add(calisanlar);
        projelerRepository.save(projeler);
    }

    public void calisanCikar(Long projeId, Long calisanId) {
        Projeler projeler = projelerRepository.findById(projeId)
                .orElseThrow(() -> new RuntimeException("Proje bulunamadı"));

        Calisanlar calisanlar = calisanlarRepository.findById(calisanId)
                .orElseThrow(() -> new RuntimeException("Çalışan bulunamadı"));

        projeler.getCalisanlar().remove(calisanlar);
        projelerRepository.save(projeler);
    }

    public void projeDurumGuncelle(Long projeId, Durum yeniDurum) {
        Projeler proje = projelerRepository.findById(projeId)
                .orElseThrow(() -> new RuntimeException("Proje bulunamadı"));

        proje.setDurum(yeniDurum);
        projelerRepository.save(proje);
    }

    // Çalışanın projelerini getir
    public List<Projeler> getProjectsByCalisan(Long calisanId) {
        try {
            Optional<Calisanlar> calisanOpt = calisanlarRepository.findById(calisanId);
            if (calisanOpt.isPresent()) {
                Calisanlar calisan = calisanOpt.get();
                // Çalışanın projelerini Set'ten List'e çevir
                return new ArrayList<>(calisan.getProjeler());
            }
            return new ArrayList<>();
        } catch (Exception e) {
            System.err.println("Çalışan projeleri getirirken hata: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    // Alternatif method name - backward compatibility için
    public List<Projeler> getProjelerByCalisan(Long calisanId) {
        return getProjectsByCalisan(calisanId);
    }

    // Proje durumu güncelleme
    public Projeler updateProjectStatus(Long projeId, Durum newStatus) {
        Projeler proje = projelerRepository.findById(projeId)
                .orElseThrow(() -> new RuntimeException("Proje bulunamadı: " + projeId));

        proje.setDurum(newStatus);
        return projelerRepository.save(proje);
    }

    // Çalışana göre projeleri filtrele
    public List<Projeler> getProjectsForUser(String username) {
        try {
            // Bu method UserService ile birlikte çalışır
            // Şimdilik boş liste döndür, implementation UserService'e bağlı
            return new ArrayList<>();
        } catch (Exception e) {
            System.err.println("Kullanıcı projeleri getirirken hata: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    // Durum filtrelemesi
    public List<Projeler> getProjectsByStatus(Durum durum) {
        if (durum == null) {
            return tumProjeleriGetir();
        }
        return projelerRepository.findByDurum(durum);
    }

    // Proje adına göre arama
    public List<Projeler> searchProjectsByName(String searchTerm) {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return tumProjeleriGetir();
        }
        return projelerRepository.findByBaslikContainingIgnoreCase(searchTerm.trim());
    }

    // Aktif projeleri getir
    public List<Projeler> getActiveProjects() {
        return getProjectsByStatus(Durum.DEVAM_EDIYOR);
    }

    // Tamamlanan projeleri getir
    public List<Projeler> getCompletedProjects() {
        return getProjectsByStatus(Durum.TAMAMLANDI);
    }

    // Proje istatistikleri
    public Map<String, Long> getProjectStatistics() {
        List<Projeler> allProjects = tumProjeleriGetir();
        Map<String, Long> stats = new HashMap<>();

        stats.put("total", (long) allProjects.size());
        stats.put("active", allProjects.stream()
                .filter(p -> Durum.DEVAM_EDIYOR.equals(p.getDurum()))
                .count());
        stats.put("completed", allProjects.stream()
                .filter(p -> Durum.TAMAMLANDI.equals(p.getDurum()))
                .count());
        stats.put("paused", allProjects.stream()
                .filter(p -> Durum.ARA_VERILDI.equals(p.getDurum()))
                .count());

        return stats;
    }
}