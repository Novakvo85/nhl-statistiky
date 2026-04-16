package org.novak.statistics.controller;


import org.novak.statistics.entity.Team;
import org.novak.statistics.service.TeamService;
import org.springframework.ui.Model;
import org.novak.statistics.entity.User;
import org.novak.statistics.service.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@Controller
public class ProfileController {


    private final UserService userService;
    private final TeamService teamService;
    private final PasswordEncoder passwordEncoder;

    public ProfileController(UserService userService, TeamService teamService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.teamService = teamService;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/profile")
    public String showProfile(Model model, Principal principal) {
        User user = userService.getByUsername(principal.getName());
        model.addAttribute("user", user);
        return "profile";
    }

    @PostMapping("/profile")
    public String updateProfile(@ModelAttribute("user") User updatedUser, @RequestParam("oldPassword") String oldPassword, @RequestParam(value = "newPassword", required = false) String newPassword, Model model, Principal principal) {

        User currentUser = userService.getByUsername(principal.getName());

        if (!passwordEncoder.matches(oldPassword, currentUser.getPassword())) {
            model.addAttribute("error", "Staré heslo není správně.");
            return "profile";
        }

        if (!updatedUser.getUsername().equals(currentUser.getUsername()) &&
                userService.existsByUsername(updatedUser.getUsername())) {
            model.addAttribute("error", "Toto uživatelské jméno je již obsazené.");
            return "profile";
        }
        if (!updatedUser.getEmail().equals(currentUser.getEmail()) &&
                userService.existsByEmail(updatedUser.getEmail())) {
            model.addAttribute("error", "Tento email je již obsazený.");
            return "profile";
        }

        currentUser.setUsername(updatedUser.getUsername());
        currentUser.setEmail(updatedUser.getEmail());

        if (newPassword != null && !newPassword.isBlank()) {
            currentUser.setPassword(passwordEncoder.encode(newPassword));
        }

        userService.save(currentUser);
        model.addAttribute("success", "Údaje byly úspěšně změněny.");
        model.addAttribute("user", currentUser);
        return "profile";
    }

    @PostMapping("/profile/dropFavorite/{id}")
    public String dropFavouriteTeam(@PathVariable Long id, Principal principal){
        if (principal != null) {
            User user = userService.getByUsername(principal.getName());
            Team team = teamService.getTeamById(id);
            userService.removeFavoriteTeam(user, team);
        }
        return "redirect:/profile";
    }


}
