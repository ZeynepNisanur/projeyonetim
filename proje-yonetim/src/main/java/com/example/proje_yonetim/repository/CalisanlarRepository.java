package com.example.proje_yonetim.repository;

import com.example.proje_yonetim.entity.Calisanlar;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CalisanlarRepository extends JpaRepository<Calisanlar, Long> {

}
