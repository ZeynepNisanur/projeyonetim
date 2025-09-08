package com.example.proje_yonetim.repository;

import com.example.proje_yonetim.entity.Calisanlar;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.List;
//import org.springframework.data.jpa.repository.Query;
//import org.springframework.data.repository.query.Param;

@Repository
public interface CalisanlarRepository extends JpaRepository<Calisanlar, Long> {

    Optional<Calisanlar> findByEposta(String eposta);

    Optional<Calisanlar> findByAd(String ad);

    List<Calisanlar> findBySoyadContainingIgnoreCase(String soyad);

    List<Calisanlar> findByAdContainingIgnoreCase(String ad);

    @org.springframework.data.jpa.repository.Query("SELECT c FROM Calisanlar c WHERE c.user.id = :userId")
    Optional<Calisanlar> findByUserId(@org.springframework.data.repository.query.Param("userId") Long userId);

}
