package edu.lysak.recipes.controller;

import edu.lysak.recipes.model.Recipe;
import edu.lysak.recipes.service.RecipeService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

@RestController
public class RecipeController {
    private final RecipeService recipeService;

    public RecipeController(RecipeService recipeService) {
        this.recipeService = recipeService;
    }

    @GetMapping("/api/recipe/{id}")
    public Recipe getRecipe(@PathVariable Long id) {
        return recipeService.getRecipe(id);
    }

    @PostMapping("/api/recipe/new")
    public Map<String, Long> addRecipe(@Valid @RequestBody Recipe recipe) {
        Long id = recipeService.addRecipe(recipe);
        return Map.of("id", id);
    }

    @DeleteMapping("/api/recipe/{id}")
    public void deleteRecipe(@PathVariable Long id) {
        recipeService.deleteRecipe(id);
        throw new ResponseStatusException(HttpStatus.NO_CONTENT);
    }

    @PutMapping("/api/recipe/{id}")
    public void updateRecipe(@PathVariable Long id, @Valid @RequestBody Recipe recipe) {
        recipeService.updateRecipe(id, recipe);
        throw new ResponseStatusException(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/api/recipe/search")
    public List<Recipe> searchRecipes(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String name
    ) {
        if ((category == null && name == null) || (category != null && name != null)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }

        if (category != null) {
            return recipeService.getRecipesByCategory(category);
        } else {
            return recipeService.getRecipesByName(name);
        }
    }
}
