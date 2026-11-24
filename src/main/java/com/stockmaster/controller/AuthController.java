package com.stockmaster.controller;

import com.stockmaster.service.AdminAuthService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AuthController {

    private final AdminAuthService authService;

    public AuthController(AdminAuthService authService) {
        this.authService = authService;
    }

    @GetMapping("/login")
    public String loginPage(@RequestParam(value = "error", required = false) String error,
                            @RequestParam(value = "logout", required = false) String logout,
                            @RequestParam(value = "redirect", required = false) String redirect,
                            HttpSession session,
                            Model model) {
        if (authService.isLoggedIn(session)) {
            return "redirect:/dashboard";
        }

        if (error != null) {
            model.addAttribute("errorMessage", "Invalid Admin username or password. Please try again.");
        }
        if (logout != null) {
            model.addAttribute("successMessage", "You have been logged out successfully.");
        }
        model.addAttribute("redirect", redirect);
        return "login";
    }

    @PostMapping("/login")
    public String processLogin(@RequestParam("username") String username,
                               @RequestParam("password") String password,
                               @RequestParam(value = "redirect", required = false) String redirect,
                               HttpSession session,
                               RedirectAttributes redirectAttributes) {
        boolean authenticated = authService.authenticate(username, password, session);
        if (authenticated) {
            redirectAttributes.addFlashAttribute("successMessage", "Welcome back, " + username + "!");
            if (redirect != null && !redirect.isBlank() && !redirect.contains("login")) {
                return "redirect:" + redirect;
            }
            return "redirect:/dashboard";
        }

        redirectAttributes.addFlashAttribute("errorMessage", "Invalid credentials. Use 'admin' / 'admin123'");
        return "redirect:/login" + (redirect != null ? "?redirect=" + redirect : "");
    }

    @GetMapping("/logout")
    public String logout(HttpSession session, RedirectAttributes redirectAttributes) {
        authService.logout(session);
        redirectAttributes.addFlashAttribute("successMessage", "You have safely logged out.");
        return "redirect:/login?logout";
    }
}
