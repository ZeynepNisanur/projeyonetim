package com.example.proje_yonetim.repository;

import com.example.proje_yonetim.entity.Role;
import com.example.proje_yonetim.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);

    Optional<User> findByEposta(String eposta);

    boolean existsByUsername(String username);

    boolean existsByEposta(String eposta);

    // Belirli role sahip kullanıcıları bulma
    List<User> findByRole(Role role);

    // Role name'e göre kullanıcıları bulma (JPQL ile)
    @org.springframework.data.jpa.repository.Query("SELECT u FROM User u WHERE u.role.name = :roleName")
    List<User> findByRoleName(@org.springframework.data.repository.query.Param("roleName") Role.RoleName roleName);

    // Role name'e göre kullanıcı sayısını bulma (JPQL ile)
    @org.springframework.data.jpa.repository.Query("SELECT COUNT(u) FROM User u WHERE u.role.name = :roleName")
    long countByRoleName(@org.springframework.data.repository.query.Param("roleName") Role.RoleName roleName);
}