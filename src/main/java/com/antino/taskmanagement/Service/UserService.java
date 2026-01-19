package com.antino.taskmanagement.Service;

import com.antino.taskmanagement.Entity.RefreshToken;
import com.antino.taskmanagement.Entity.User;
import com.antino.taskmanagement.Repository.RefreshTokenRepository;
import com.antino.taskmanagement.Repository.UserRepository;
import com.antino.taskmanagement.Security.JwtUtil;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
@Service
@Transactional
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RefreshTokenRepository refreshRepo;

    @Autowired
    private JwtUtil jwtUtil;

    private BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();


    @Transactional
    public Map<String, Object> login(String email, String password) {

        User user = userRepository.findByEmail(email);

        if (user == null || !encoder.matches(password, user.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }

        String accessToken = jwtUtil.generateAccessToken(email);
        String refreshToken = jwtUtil.generateRefreshToken(email);

        refreshRepo.deleteByEmail(email);   // remove old token

        RefreshToken rt = new RefreshToken();
        rt.setToken(refreshToken);
        rt.setEmail(email);
        rt.setExpiry(new Date(System.currentTimeMillis() + 604800000));
        refreshRepo.save(rt);

        Map<String, Object> res = new HashMap<>();
        res.put("accessToken", accessToken);
        res.put("refreshToken", refreshToken);
        res.put("user", user);

        return res;
    }



    public User register(User user) {

        if (userRepository.findByEmail(user.getEmail()) != null) {
            throw new RuntimeException("User already exists");
        }

        String password = user.getPassword();

        if (!password.matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{8,}$")) {
            throw new RuntimeException(
                    "Password must have uppercase, lowercase, number, 8+ chars"
            );
        }

        user.setPassword(encoder.encode(password));
        return userRepository.save(user);
    }


    public User getCurrentUser(String token) {

        String email = jwtUtil.getEmailFromToken(token);

        User user = userRepository.findByEmail(email);

        if (user == null) {
            throw new RuntimeException("No user found");
        }

        return user;
    }


    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
}
