package com.antino.taskmanagement.Service;

import com.antino.taskmanagement.Entity.User;
import com.antino.taskmanagement.Repository.UserRepository;
import com.antino.taskmanagement.Security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminService {

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private JwtUtil jwtUtil;

    // Check admin
    private void checkAdmin(String token) {
        String email = jwtUtil.getEmailFromToken(token);
        User admin = userRepo.findByEmail(email);

        if (!"admin".equalsIgnoreCase(admin.getRole())) {
            throw new RuntimeException("Admin access only");
        }
    }

    // 1. Get all users
    public List<User> getAllUsers(String token) {
        checkAdmin(token);
        return userRepo.findAll();
    }

    // 2. Update role
    public User updateUserRole(Long userId, String role, String token) {
        checkAdmin(token);

        User user = userRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setRole(role);
        return userRepo.save(user);
    }

    // 3. Delete user
    public String deleteUser(Long userId, String token) {
        checkAdmin(token);

        userRepo.deleteById(userId);
        return "User deleted successfully";
    }
}
