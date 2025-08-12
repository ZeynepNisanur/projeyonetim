package com.example.proje_yonetim.repository;

import com.example.proje_yonetim.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUseradi(String useradi);

    @Query("SELECT u FROM User u WHERE u.useradi = ?1 AND u.enabled = true")
    Optional<User> findActiveUserByUseradi(String useradi);
}