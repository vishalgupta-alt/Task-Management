package com.antino.taskmanagement.Controller;

import com.antino.taskmanagement.Entity.RefreshToken;
import com.antino.taskmanagement.Entity.User;
import com.antino.taskmanagement.Repository.RefreshTokenRepository;
import com.antino.taskmanagement.Security.JwtUtil;
import com.antino.taskmanagement.Service.UserService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private RefreshTokenRepository refreshRepo;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/register")
    public User register(@RequestBody User user){
        return userService.register(user);
    }

    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody User user){
        return userService.login(user.getEmail(), user.getPassword());
    }

    @GetMapping("/me")
    public User getProfile(@RequestHeader("Authorization") String authHeader){

        if(authHeader == null || !authHeader.startsWith("Bearer ")){
            throw new RuntimeException("Unauthorized");
        }

        String token = authHeader.replace("Bearer ", "");
        return userService.getCurrentUser(token);
    }

    @PostMapping("/logout")
    public String logoutUser(@RequestHeader("Authorization") String authHeader){

        if(authHeader == null || !authHeader.startsWith("Bearer ")){
            throw new RuntimeException("Unauthorized");
        }

        return "Logout successful";
    }

    @PostMapping("/refresh")
    public Map<String, Object> refresh(@RequestBody Map<String, String> body) {

        String refreshToken = body.get("refreshToken");

        RefreshToken saved = refreshRepo.findByToken(refreshToken);

        if (saved == null || saved.getExpiry().before(new Date())) {
            throw new RuntimeException("Invalid refresh token");
        }

        String newAccessToken =
                jwtUtil.generateAccessToken(saved.getEmail());

        Map<String, Object> res = new HashMap<>();
        res.put("accessToken", newAccessToken);

        return res;
    }
}
