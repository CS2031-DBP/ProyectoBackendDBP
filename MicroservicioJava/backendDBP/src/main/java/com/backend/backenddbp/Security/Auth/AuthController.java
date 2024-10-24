package com.backend.backenddbp.Security.Auth;

import com.backend.backenddbp.Security.Auth.DTOS.AuthJwtResponse;
import com.backend.backenddbp.Security.Auth.DTOS.AuthLoginRequest;
import com.backend.backenddbp.Security.Auth.DTOS.AuthRegisterRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<AuthJwtResponse> login(@RequestBody AuthLoginRequest authLoginRequest) {
        System.out.println("Login successful for user: ");
        return ResponseEntity.ok(authService.login(authLoginRequest));
    }
//
    @PostMapping("/register")
    public ResponseEntity<AuthJwtResponse> register(@RequestBody AuthRegisterRequest authRegisterRequest) {
        try {
            AuthJwtResponse response = authService.register(authRegisterRequest);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
        }
    }
    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader("Authorization") String authorizationHeader) {
        if (authService.logout(authorizationHeader)) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.status(400).body("Authorization header missing or invalid");
        }
    }
}
