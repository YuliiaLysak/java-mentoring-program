package edu.lysak.recipes.controller;

import edu.lysak.recipes.dto.RecipeDto;
import edu.lysak.recipes.model.Recipe;
import edu.lysak.recipes.service.RecipeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public Map<String, Long> addRecipe(@Valid @RequestBody RecipeDto recipeDto) {
        Long id = recipeService.addRecipe(recipeDto);
        return Map.of("id", id);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/api/recipe/{id}")
    public void deleteRecipe(@PathVariable Long id) {
        recipeService.deleteRecipe(id);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PutMapping("/api/recipe/{id}")
    public void updateRecipe(@PathVariable Long id, @Valid @RequestBody RecipeDto recipeDto) {
        recipeService.updateRecipe(id, recipeDto);
    }

    @GetMapping("/api/recipe/search")
    public ResponseEntity<List<Recipe>> searchRecipes(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String name
    ) {
        if ((category == null && name == null) || (category != null && name != null)) {
            return ResponseEntity.badRequest().build();
        }

        if (category != null) {
            return ResponseEntity.ok(recipeService.getRecipesByCategory(category));
        } else {
            return ResponseEntity.ok(recipeService.getRecipesByName(name));
        }
    }
}
