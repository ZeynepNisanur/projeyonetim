package com.example.proje_yonetim.repository;

import com.example.proje_yonetim.entity.Role;
import com.example.proje_yonetim.entity.Role.RoleName;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

    Optional<Role> findByName(RoleName name);
    
    // String ile de arama yapabilmek için
    default Optional<Role> findByName(String name) {
        try {
            return findByName(RoleName.valueOf(name.toUpperCase()));
        } catch (IllegalArgumentException e) {
            return Optional.empty();
        }
    }

    boolean existsByName(RoleName name);
    
    // String ile de kontrol edebilmek için
    default boolean existsByName(String name) {
        try {
            return existsByName(RoleName.valueOf(name.toUpperCase()));
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}
