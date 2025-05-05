package com.OMS.auth.controller;

import com.OMS.auth.model.user;
import com.OMS.auth.service.authService;
import com.OMS.auth.tool.jwt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class authCtrller {
    @Autowired
    private authService authService;

    @Autowired
    private jwt jwt;

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestParam String username, @RequestParam String password) {
        authService.register(username, password);
        return ResponseEntity.ok("User registered successfully");
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestParam String username, @RequestParam String password) {
        user user = authService.authenticate(username, password);
        if (user != null) {
            String token = jwt.generateToken(user.getUserName(), user.getRole());
            return ResponseEntity.ok(token);
        } else {
            return ResponseEntity.status(401).body("Invalid credentials");
        }
    }
}
