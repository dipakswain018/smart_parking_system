package com.campus.parking.security;

import com.campus.parking.entity.AppUser;
import com.campus.parking.repository.UserRepository;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository users;
    public CustomUserDetailsService(UserRepository users){this.users=users;}
    @Override public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        AppUser user=users.findByEmailIgnoreCase(email).orElseThrow(()->new UsernameNotFoundException("User not found"));
        return User.withUsername(user.getEmail()).password(user.getPassword())
                .authorities("ROLE_"+user.getRole().name()).disabled(!user.isEnabled()).build();
    }
}
