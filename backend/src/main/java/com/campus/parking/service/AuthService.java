package com.campus.parking.service;

import com.campus.parking.dto.*;
import com.campus.parking.entity.AppUser;
import com.campus.parking.model.Role;
import com.campus.parking.repository.UserRepository;
import com.campus.parking.security.JwtService;
import org.springframework.security.authentication.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private final AuthenticationManager manager; private final UserRepository users; private final JwtService jwt; private final PasswordEncoder encoder;
    public AuthService(AuthenticationManager manager,UserRepository users,JwtService jwt,PasswordEncoder encoder){this.manager=manager;this.users=users;this.jwt=jwt;this.encoder=encoder;}
    public LoginResponse login(LoginRequest request){
        manager.authenticate(new UsernamePasswordAuthenticationToken(request.email(),request.password()));
        AppUser u=users.findByEmailIgnoreCase(request.email()).orElseThrow();
        var details=User.withUsername(u.getEmail()).password(u.getPassword()).authorities("ROLE_"+u.getRole()).build();
        return new LoginResponse(jwt.generate(details),"Bearer",jwt.expirationMs(),
                new LoginResponse.UserView(u.getId(),u.getFullName(),u.getEmail(),u.getRole().name()));
    }

    public LoginResponse register(RegisterRequest request){
        if(users.existsByEmailIgnoreCase(request.email().trim())){
            throw new ResponseStatusException(HttpStatus.CONFLICT, "An account with this email already exists.");
        }
        AppUser user = new AppUser();
        user.setFullName(request.fullName().trim());
        user.setEmail(request.email().trim().toLowerCase());
        user.setPassword(encoder.encode(request.password()));
        // Public registration can ONLY create a customer account.
        user.setRole(Role.USER);
        user = users.save(user);
        var details=User.withUsername(user.getEmail()).password(user.getPassword()).authorities("ROLE_USER").build();
        return new LoginResponse(jwt.generate(details),"Bearer",jwt.expirationMs(),
                new LoginResponse.UserView(user.getId(),user.getFullName(),user.getEmail(),user.getRole().name()));
    }
    public LoginResponse.UserView current(String email){
        AppUser u=users.findByEmailIgnoreCase(email).orElseThrow();
        return new LoginResponse.UserView(u.getId(),u.getFullName(),u.getEmail(),u.getRole().name());
    }
}
