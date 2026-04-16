package org.novak.statistics.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import org.novak.statistics.entity.User;
import org.novak.statistics.service.UserService;
import org.springframework.ui.Model;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;

@Controller
public class MainController {

    private static final Logger log = LoggerFactory.getLogger(MainController.class);

    private final UserService userService;

    public MainController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/")
    public String rootRedirect(Principal principal) {
        if (principal != null) {
            return "redirect:/my-games";
        } else {
            return "redirect:/games";
        }
    }


    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }

    @PostMapping("/register")
    public String processRegister(@ModelAttribute("user") User user, @RequestParam String confirmPassword, Model model, HttpServletRequest request) {
        try {
            if (!user.getPassword().equals(confirmPassword)) {
                model.addAttribute("error", "Hesla se neshodují.");
                return "register";
            }

            if (userService.existsByUsername(user.getUsername())) {
                model.addAttribute("error", "Uživatel s tímto uživatelským jménem již existuje.");
                return "register";
            }

            if (userService.existsByEmail(user.getEmail())) {
                model.addAttribute("error", "Účet s tímto emailem již existuje.");
                return "register";
            }


            userService.registerUser(user.getUsername(), user.getEmail(), user.getPassword());

            try {
                request.login(user.getUsername(), user.getPassword());
                return "redirect:/my-games";
            } catch (ServletException e) {
                log.error("Auto-login po registraci selhal: {}", e.getMessage());
                return "redirect:/login?registered";
            }

        } catch (IllegalArgumentException e) {
            log.warn("Chyba při registraci uživatele '{}': {}", user.getUsername(), e.getMessage());
            model.addAttribute("error", e.getMessage());
            return "register";
        }

    }
}