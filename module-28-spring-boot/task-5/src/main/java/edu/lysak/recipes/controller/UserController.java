package edu.lysak.recipes.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class UserController {

    @GetMapping("/info")
    @PreAuthorize("hasRole('ROLE_USER')")
    public String getRecipesInterestingStatistic() {
        return "recipes-statistics";
    }

    @GetMapping("/about")
    public String getInfoForUnauthUsers() {
        return "recipes-statistics-non-auth";
    }

    @GetMapping("/admin")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public String getAdminPage() {
        return "admin-page";
    }

    @GetMapping("/login")
    public String getLoginPage() {
        return "login";
    }

    @GetMapping("/logout-success")
    public String getLogoutPage() {
        return "logout";
    }

}
