package com.example.proje_yonetim.service;

import com.example.proje_yonetim.entity.Calisanlar;
import com.example.proje_yonetim.repository.CalisanlarRepository;
//import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CalisanlarService {

    private final CalisanlarRepository calisanlarRepository; // final olarak tanımladım başka yerden değiştirilemez
                                                             // constructor içinde atanabileceğini garantiye aldık.

    public CalisanlarService(CalisanlarRepository calisanlarRepository) {
        this.calisanlarRepository = calisanlarRepository;
    }

    public List<Calisanlar> getAllCalisanlar() {
        return calisanlarRepository.findAll();
    }

    public Calisanlar getCalisanById(Long id) {
        return calisanlarRepository.findById(id).orElse(null);
    }

    public Calisanlar createCalisan(Calisanlar calisan) {
        return calisanlarRepository.save(calisan);
    }

    public Calisanlar updateCalisan(Long id, Calisanlar yeniCalisan) {
        Optional<Calisanlar> optionalCalisan = calisanlarRepository.findById(id);
        if (optionalCalisan.isPresent()) {
            Calisanlar mevcut = optionalCalisan.get();
            mevcut.setAd(yeniCalisan.getAd());
            mevcut.setSoyad(yeniCalisan.getSoyad());
            mevcut.setEposta(yeniCalisan.getEposta());
            mevcut.setPozisyon(yeniCalisan.getPozisyon());
            return calisanlarRepository.save(mevcut);
        } else {
            return null;
        }
    }
    // mevcut çalışanın verilerini yeni gelen bilgilerle günceller, çalışan(id)
    // yoksa null döndürür.

    public void deleteCalisan(Long id) {
        calisanlarRepository.deleteById(id);
    }
}