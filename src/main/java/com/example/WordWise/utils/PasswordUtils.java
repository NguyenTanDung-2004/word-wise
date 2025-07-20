package com.example.WordWise.utils;


import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class PasswordUtils {
    private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public String hashPassword(String password) {
        return passwordEncoder.encode(password);
    }

    public int checkPassword(String passwordWithOutHash, String hashedPassword) {
        if (passwordEncoder.matches(passwordWithOutHash, hashedPassword) == true) {
            return 1;
        }
        return 0;
    }
}
