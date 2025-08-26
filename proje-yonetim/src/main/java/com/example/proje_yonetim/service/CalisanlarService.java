package com.example.proje_yonetim.service;

import com.example.proje_yonetim.entity.Calisanlar;
import com.example.proje_yonetim.repository.CalisanlarRepository;
import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
import com.example.proje_yonetim.entity.Role;
import com.example.proje_yonetim.entity.User;
import com.example.proje_yonetim.repository.UserRepository;
import com.example.proje_yonetim.repository.RoleRepository;

import java.util.List;
import java.util.Optional;

@Service
public class CalisanlarService {
    private final CalisanlarRepository calisanlarRepository; // final olarak tanımladım başka yerden değiştirilemez
    // constructor içinde atanabileceğini garantiye aldık.
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    public CalisanlarService(CalisanlarRepository calisanlarRepository, UserRepository userRepository,
            RoleRepository roleRepository) {
        this.calisanlarRepository = calisanlarRepository;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    public List<Calisanlar> getAllCalisanlar() {
        List<Calisanlar> calisanlar = calisanlarRepository.findAll();
        System.out.println("Bulunan çalışan sayısı: " + calisanlar.size()); // Debug log
        return calisanlar;
    }

    public Calisanlar getCalisanById(Long id) {
        return calisanlarRepository.findById(id).orElse(null);
    }

    public Calisanlar createCalisan(Calisanlar calisan) {
        // Eğer User bilgisi varsa, User'ı da kaydet
        if (calisan.getUser() != null) {
            User user = calisan.getUser();

            // Role set edilmemişse, varsayılan USER role'ü ata
            if (user.getRole() == null) {
                Role userRole = roleRepository.findByName(Role.RoleName.USER)
                        .orElseThrow(() -> new RuntimeException("USER role not found"));
                user.setRole(userRole);
            }

            // User'ı kaydet
            user = userRepository.save(user);
            calisan.setUser(user);
        }

        // Role set edilmemişse, varsayılan USER role'ü ata
        if (calisan.getRole() == null) {
            Role userRole = roleRepository.findByName(Role.RoleName.USER)
                    .orElseThrow(() -> new RuntimeException("USER role not found"));
            calisan.setRole(userRole);
        }

        Calisanlar savedCalisan = calisanlarRepository.save(calisan);
        System.out.println("Yeni çalışan kaydedildi: " + savedCalisan.getId()); // Debug log
        return savedCalisan;
    }

    public Calisanlar updateCalisan(Long id, Calisanlar calisan) {
        Optional<Calisanlar> existingCalisanOpt = calisanlarRepository.findById(id);

        if (existingCalisanOpt.isPresent()) {
            Calisanlar existingCalisan = existingCalisanOpt.get();

            // Çalışan bilgilerini güncelle
            existingCalisan.setAd(calisan.getAd());
            existingCalisan.setSoyad(calisan.getSoyad());
            existingCalisan.setEposta(calisan.getEposta());
            existingCalisan.setPozisyon(calisan.getPozisyon());

            // Role güncelle
            if (calisan.getRole() != null) {
                existingCalisan.setRole(calisan.getRole());
            }

            // User bilgisi güncelle
            if (calisan.getUser() != null) {
                User existingUser = existingCalisan.getUser();
                User newUser = calisan.getUser();

                if (existingUser != null) {
                    // Mevcut user'ı güncelle
                    existingUser.setUsername(newUser.getUsername());
                    existingUser.setEposta(newUser.getEposta());
                    if (newUser.getSifre() != null && !newUser.getSifre().isEmpty()) {
                        existingUser.setSifre(newUser.getSifre());
                    }
                    if (newUser.getRole() != null) {
                        existingUser.setRole(newUser.getRole());
                    }
                    userRepository.save(existingUser);
                } else {
                    // Yeni user oluştur
                    if (newUser.getRole() == null) {
                        Role userRole = roleRepository.findByName(Role.RoleName.USER)
                                .orElseThrow(() -> new RuntimeException("USER role not found"));
                        newUser.setRole(userRole);
                    }
                    User savedUser = userRepository.save(newUser);
                    existingCalisan.setUser(savedUser);
                }
            }

            return calisanlarRepository.save(existingCalisan);
        }
        return null;
    }

    public void deleteCalisan(Long id) {
        Optional<Calisanlar> calisanOpt = calisanlarRepository.findById(id);
        if (calisanOpt.isPresent()) {
            Calisanlar calisan = calisanOpt.get();

            // İlişkili User'ı da sil (eğer sadece bu çalışana ait ise)
            if (calisan.getUser() != null) {
                userRepository.delete(calisan.getUser());
            }

            calisanlarRepository.deleteById(id);
            System.out.println("Çalışan silindi: " + id); // Debug log
        }
    }

    // User ile çalışan arasında bağlantı kurma
    public Calisanlar linkCalisanToUser(Long calisanId, Long userId) {
        Optional<Calisanlar> calisanOpt = calisanlarRepository.findById(calisanId);
        Optional<User> userOpt = userRepository.findById(userId);

        if (calisanOpt.isPresent() && userOpt.isPresent()) {
            Calisanlar calisan = calisanOpt.get();
            User user = userOpt.get();

            calisan.setUser(user);
            return calisanlarRepository.save(calisan);
        }
        throw new RuntimeException("Çalışan veya User bulunamadı");
    }

    // E-posta ile çalışan bulma
    public Optional<Calisanlar> findByEposta(String eposta) {
        return calisanlarRepository.findByEposta(eposta);
    }

    // User ID ile çalışan bulma
    public Optional<Calisanlar> findByUserId(Long userId) {
        return calisanlarRepository.findByUserId(userId);
    }

    // Çalışan sayısını döndür (debug için)
    public long getCalisanSayisi() {
        long count = calisanlarRepository.count();
        System.out.println("Toplam çalışan sayısı: " + count); // Debug log
        return count;
    }
}