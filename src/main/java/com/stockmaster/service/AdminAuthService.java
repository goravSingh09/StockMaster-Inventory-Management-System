package com.stockmaster.service;

import com.stockmaster.entity.Admin;
import com.stockmaster.repository.AdminRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AdminAuthService {

    public static final String SESSION_ADMIN_KEY = "LOGGED_IN_ADMIN";

    private final AdminRepository adminRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public AdminAuthService(AdminRepository adminRepository) {
        this.adminRepository = adminRepository;
    }

    public boolean authenticate(String username, String rawPassword, HttpSession session) {
        if (username == null || rawPassword == null) {
            return false;
        }

        Optional<Admin> adminOpt = adminRepository.findByUsername(username.trim());
        if (adminOpt.isPresent()) {
            Admin admin = adminOpt.get();
            if (passwordEncoder.matches(rawPassword, admin.getPasswordHash())) {
                session.setAttribute(SESSION_ADMIN_KEY, admin);
                return true;
            }
        }
        return false;
    }

    public void logout(HttpSession session) {
        if (session != null) {
            session.removeAttribute(SESSION_ADMIN_KEY);
            session.invalidate();
        }
    }

    public boolean isLoggedIn(HttpSession session) {
        return session != null && session.getAttribute(SESSION_ADMIN_KEY) != null;
    }

    public Admin getLoggedInAdmin(HttpSession session) {
        if (session == null) {
            return null;
        }
        return (Admin) session.getAttribute(SESSION_ADMIN_KEY);
    }

    public String hashPassword(String rawPassword) {
        return passwordEncoder.encode(rawPassword);
    }
}
