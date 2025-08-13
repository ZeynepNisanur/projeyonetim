package com.example.proje_yonetim.service;

import com.example.proje_yonetim.entity.Calisanlar;
import com.example.proje_yonetim.repository.CalisanlarRepository;
//import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;

//import java.util.Map;
//import java.util.HashMap;
import java.util.List;
//import java.util.Optional;

/*@Service
public class CalisanlarService {

    @Autowired
    private CalisanlarRepository calisanlarRepository;

    // Toplam çalışan sayısı
    public long getToplamCalisanSayisi() {
        return calisanlarRepository.count();
    }

    // Tüm çalışanları getir
    public List<Calisanlar> getTumCalisanlar() {
        return calisanlarRepository.findAll();
    }

    // Son eklenen çalışanları getir
    public List<Calisanlar> getSonEklenenCalisanlar(int limit) {
        return calisanlarRepository.findTop5ByOrderByIdDesc();
    }

    // Çalışan ara
    public List<Calisanlar> calisanAra(String anahtar) {
        return calisanlarRepository.findByAdContainingIgnoreCaseOrSoyadContainingIgnoreCase(anahtar, anahtar);
    }

    // Çalışan detaylarını getir (projeler dahil)
    public Map<String, Object> getCalisanDetay(Long calisanId) {
        Map<String, Object> detay = new HashMap<>();
        
        Calisanlar calisanlar = calisanlarRepository.findById(calisanId).orElse(null);
        if (calisanlar != null) {
            detay.put("calisan", calisanlar);
            // Çalışanın projelerini de ekleyebilirsiniz
            detay.put("projeSayisi", calisanlarRepository.countProjesByCalisanId(calisanId));
        }
        
        return detay;
    }

    // Departmana göre çalışanları getir
    public List<Calisanlar> getCalisanlarByDepartman(String departman) {
        return calisanlarRepository.findByDepartman(departman);
    }

    // Yeni çalışan ekle
    public Calisanlar calisanEkle(Calisanlar calisanlar) {
        return calisanlarRepository.save(calisanlar);
    }

    // Çalışan güncelle
    public Calisanlar calisanGuncelle(Long id, Calisanlar calisanlar) {
        if (calisanlarRepository.existsById(id)) {
            calisanlar.setId(id);
            return calisanlarRepository.save(calisanlar);
        }
        return null;
    }

    // Çalışan sil
    public boolean calisanSil(Long id) {
        if (calisanlarRepository.existsById(id)) {
            calisanlarRepository.deleteById(id);
            return true;
        }
        return false;
    }
}*/

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

    public Calisanlar updateCalisan(Long id, Calisanlar calisan) {
        if (calisanlarRepository.existsById(id)) {
            calisan.setId(id); // Entity’deki id alanına set et
            return calisanlarRepository.save(calisan);
        }
        return null;

    }
    // mevcut çalışanın verilerini yeni gelen bilgilerle günceller, çalışan(id)
    // yoksa null döndürür.

    public void deleteCalisan(Long id) {
        calisanlarRepository.deleteById(id);
    }
}