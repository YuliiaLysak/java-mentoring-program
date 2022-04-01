package edu.lysak.recipes.service;

import edu.lysak.recipes.exception.RecipeNotFoundException;
import edu.lysak.recipes.model.Recipe;
import edu.lysak.recipes.repository.RecipeRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class RecipeService {
    private final RecipeRepository recipeRepository;

    public RecipeService(RecipeRepository recipeRepository) {
        this.recipeRepository = recipeRepository;
    }

    public Recipe getRecipe(Long id) {
        return getRecipeFromDb(id);
    }

    public Long addRecipe(Recipe recipe) {
        recipe.setDate(LocalDateTime.now());
        return recipeRepository.save(recipe).getId();
    }

    public void deleteRecipe(Long recipeId) {
        Recipe recipe = getRecipeFromDb(recipeId);
        recipeRepository.delete(recipe);
    }

    @Transactional
    public void updateRecipe(Long recipeId, Recipe recipe) {
        Recipe oldRecipe = getRecipeFromDb(recipeId);
        mapRecipe(recipe, oldRecipe);
        recipeRepository.save(oldRecipe);
    }

    public List<Recipe> getRecipesByCategory(String category) {
        return recipeRepository.findAllByCategoryIgnoreCaseOrderByDateDesc(category);
    }

    public List<Recipe> getRecipesByName(String name) {
        return recipeRepository.findAllByNameContainingIgnoreCaseOrderByDateDesc(name);
    }

    private Recipe getRecipeFromDb(Long recipeId) {
        return recipeRepository.findById(recipeId)
                .orElseThrow(() -> new RecipeNotFoundException(String.format("Recipe with id=%s not found", recipeId)));
    }

    private void mapRecipe(Recipe recipe, Recipe oldRecipe) {
        oldRecipe.setDate(LocalDateTime.now());
        oldRecipe.setName(recipe.getName());
        oldRecipe.setCategory(recipe.getCategory());
        oldRecipe.setDescription(recipe.getDescription());
        oldRecipe.setIngredients(recipe.getIngredients());
        oldRecipe.setDirections(recipe.getDirections());
    }
}
