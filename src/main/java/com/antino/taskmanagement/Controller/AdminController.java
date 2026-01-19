package com.antino.taskmanagement.Controller;

import com.antino.taskmanagement.Entity.User;
import com.antino.taskmanagement.Service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin")
public class AdminController {

    @Autowired
    private AdminService adminService;

    private String extractToken(String header) {
        if (header == null || !header.startsWith("Bearer ")) {
            throw new RuntimeException("Unauthorized");
        }
        return header.replace("Bearer ", "");
    }

    // 1. Get all users
    @GetMapping("/users")
    public List<User> getAllUsers(@RequestHeader("Authorization") String authHeader) {
        return adminService.getAllUsers(extractToken(authHeader));
    }

    // 2. Update user role
    @PutMapping("/users/{id}/role")
    public User updateRole(
            @PathVariable Long id,
            @RequestParam String role,
            @RequestHeader("Authorization") String authHeader) {

        return adminService.updateUserRole(id, role, extractToken(authHeader));
    }

    // 3. Delete user
    @DeleteMapping("/users/{id}")
    public String deleteUser(
            @PathVariable Long id,
            @RequestHeader("Authorization") String authHeader) {

        return adminService.deleteUser(id, extractToken(authHeader));
    }
}
