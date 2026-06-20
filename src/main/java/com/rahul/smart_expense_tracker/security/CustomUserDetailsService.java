package com.rahul.smart_expense_tracker.security;

import com.rahul.smart_expense_tracker.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import com.rahul.smart_expense_tracker.entity.User;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    // Spring Security calls this method during authentication
    // "username" here is actually the email (we use email to login)
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "User not found with email: " + email
                ));

        // Convert our User entity into Spring Security's UserDetails object
        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),                    // username (we use email)
                user.getPassword(),                 // BCrypt hashed password
                Collections.singletonList(          // authorities/roles
                        new SimpleGrantedAuthority(user.getUserRole().name())
                )
        );
    }

}
