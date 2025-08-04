package com.example.proje_yonetim.repository;

import com.example.proje_yonetim.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByUseradi(String useradi);
}