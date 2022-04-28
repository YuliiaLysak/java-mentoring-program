package edu.lysak.recipes.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class UserController {

    @GetMapping("/info")
    public String getRecipesInterestingStatistic() {
        return "recipes-statistics";
    }

    @GetMapping("/about")
    public String getInfoForUnauthUsers() {
        return "recipes-statistics-non-auth";
    }

    @GetMapping("/admin")
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
