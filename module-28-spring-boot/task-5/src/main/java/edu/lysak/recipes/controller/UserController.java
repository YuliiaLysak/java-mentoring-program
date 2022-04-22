package edu.lysak.recipes.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class UserController {

    @GetMapping("/info")
    public String getRecipesInterestingStatistic() {
        return "recipes-statistics";
    }

}
