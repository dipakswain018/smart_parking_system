package com.campus.parking.controller;
import com.campus.parking.dto.*;
import com.campus.parking.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
@RestController @RequestMapping("/api/auth")
public class AuthController {
    private final AuthService service;
    public AuthController(AuthService service){this.service=service;}
    @PostMapping("/login") public LoginResponse login(@Valid @RequestBody LoginRequest req){return service.login(req);}
    @PostMapping("/register") public LoginResponse register(@Valid @RequestBody RegisterRequest req){return service.register(req);}
    @GetMapping("/me") public LoginResponse.UserView me(Authentication auth){return service.current(auth.getName());}
}
