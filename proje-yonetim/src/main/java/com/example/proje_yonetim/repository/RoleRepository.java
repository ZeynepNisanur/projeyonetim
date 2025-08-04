package com.example.proje_yonetim.repository;

import com.example.proje_yonetim.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

//temel CRUD operasyonlarını gerçekleştirir. 
public interface RoleRepository extends JpaRepository<Role, Long> {
    Role findByName(String name);
}
