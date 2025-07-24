package com.example.proje_yonetim.service;

import com.example.proje_yonetim.entity.Projeler;
import com.example.proje_yonetim.repository.ProjelerRepository;
import org.springframework.stereotype.Service;

//import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class ProjelerService {
    private final ProjelerRepository projelerRepository;

    public ProjelerService(ProjelerRepository projelerRepository) {
        this.projelerRepository = projelerRepository;
    }

    public List<Projeler> tumProjeleriGetir() {
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
}