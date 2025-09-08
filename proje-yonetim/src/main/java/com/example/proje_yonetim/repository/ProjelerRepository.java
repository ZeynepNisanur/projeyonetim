package com.example.proje_yonetim.repository;

import com.example.proje_yonetim.entity.Durum;
import com.example.proje_yonetim.entity.Projeler;
import org.springframework.data.jpa.repository.JpaRepository;
//import java.time.LocalDate;
import java.util.List;
//import java.util.Optional;

public interface ProjelerRepository extends JpaRepository<Projeler, Long> {
    List<Projeler> findByDurum(Durum durum);

    List<Projeler> findByBaslikContainingIgnoreCase(String baslik);

}