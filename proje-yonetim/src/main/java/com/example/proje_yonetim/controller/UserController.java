package com.example.proje_yonetim.controller;

import com.example.proje_yonetim.dto.CreateUserRequest;
import com.example.proje_yonetim.entity.Role;
import com.example.proje_yonetim.entity.User;
import com.example.proje_yonetim.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/create")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<User> createUser(@RequestBody CreateUserRequest request) {
        User user = userService.createUser(
                request.getKullaniciadi(),
                request.getSifre(),
                request.getEposta());
        return ResponseEntity.ok(user);
    }

    @PostMapping("/create-admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<User> createAdmin(@RequestBody CreateUserRequest request) {
        User user = userService.createUserWithRole(
                request.getKullaniciadi(),
                request.getSifre(),
                request.getEposta(),
                Role.RoleName.ADMIN);
        return ResponseEntity.ok(user);
    }

    @PutMapping("/{userId}/promote")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<User> promoteToAdmin(@PathVariable Long userId) {
        User user = userService.promoteToAdmin(userId);
        return ResponseEntity.ok(user);
    }

    @PutMapping("/{userId}/demote")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<User> demoteToUser(@PathVariable Long userId) {
        User user = userService.demoteToUser(userId);
        return ResponseEntity.ok(user);
    }

    @GetMapping("/admins")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<User>> getAllAdmins() {
        List<User> admins = userService.getAllAdmins();
        return ResponseEntity.ok(admins);
    }

    @GetMapping("/users")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }
}