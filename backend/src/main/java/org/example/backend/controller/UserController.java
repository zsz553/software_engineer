package org.example.backend.controller;

import org.example.backend.dto.UserRequest;
import org.example.backend.entity.User;
import org.example.backend.repository.UserRepository;
import org.springframework.web.bind.annotation.*;
import org.example.backend.dto.LoginResponse;
import org.example.backend.util.JwtUtil;

@RestController
@RequestMapping("/users")
@CrossOrigin(origins = "http://localhost:5173")
public class UserController {

    private final UserRepository userRepository;

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @PostMapping("/register")
    public String register(@RequestBody UserRequest request) {

        User existingUser = userRepository.findByUsername(request.getUsername());

        if (existingUser != null) {
            return "Username already exists.";
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(request.getPassword());

        userRepository.save(user);

        return "Registered successfully";
    }

    @PostMapping("/login")
    public Object login(@RequestBody UserRequest request) {
        User user = userRepository.findByUsername(request.getUsername());
        if (user == null) {
            return "The user does not exist.";
        }
        if (!user.getPassword().equals(request.getPassword())) {
            return "Password Incorrect";
        }
        String token = JwtUtil.generateToken(user.getUsername());
        return new LoginResponse(
                "Login successfully",
                user.getUsername(),
                token
        );
    }

    @GetMapping("/info")
    public User getUserInfo(@RequestParam String username) {
        return userRepository.findByUsername(username);
    }
}