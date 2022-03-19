package edu.lysak.recipes.service;

import edu.lysak.recipes.dto.IngredientDto;
import edu.lysak.recipes.dto.RecipeDto;
import edu.lysak.recipes.model.Ingredient;
import edu.lysak.recipes.model.Recipe;
import edu.lysak.recipes.model.RecipesIngredients;
import edu.lysak.recipes.repository.IngredientRepository;
import edu.lysak.recipes.repository.RecipeRepository;
import edu.lysak.recipes.repository.RecipesIngredientsRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RecipeService {
    private final RecipeRepository recipeRepository;
    private final IngredientRepository ingredientsRepository;
    private final RecipesIngredientsRepository recipesIngredientsRepository;

    public RecipeService(
            RecipeRepository recipeRepository,
            IngredientRepository ingredientsRepository,
            RecipesIngredientsRepository recipesIngredientsRepository
    ) {
        this.recipeRepository = recipeRepository;
        this.ingredientsRepository = ingredientsRepository;
        this.recipesIngredientsRepository = recipesIngredientsRepository;
    }

    public Recipe getRecipe(Long id) {
        return recipeRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @Transactional
    public Long addRecipe(RecipeDto recipeDto) {
        Recipe recipe = Recipe.builder()
                .name(recipeDto.getName())
                .category(recipeDto.getCategory())
                .date(LocalDateTime.now())
                .description(recipeDto.getDescription())
                .directions(recipeDto.getDirections())
                .build();
        Long recipeId = recipeRepository.save(recipe).getId();

        List<IngredientDto> ingredientsDto = recipeDto.getIngredients();
        List<RecipesIngredients> recipesIngredients = ingredientsDto.stream()
                .map(it -> RecipesIngredients.builder()
                        .recipeId(recipeId)
                        .ingredient(ingredientsRepository.findByName(it.getName()))
                        .quantity(it.getQuantity())
                        .measurementUnit(it.getMeasurementUnit())
                        .build())
                .collect(Collectors.toList());

        recipesIngredientsRepository.saveAll(recipesIngredients);
        return recipeId;
    }

    public void deleteRecipe(Long recipeId) {
        recipeRepository.deleteRecipe(recipeId);
    }

    public void updateRecipe(Long recipeId, RecipeDto recipeDto) {
        updateIngredients(recipeId, recipeDto.getIngredients());
        recipeRepository.updateRecipe(
                recipeId,
                recipeDto.getName(),
                recipeDto.getCategory(),
                LocalDateTime.now(),
                recipeDto.getDescription(),
                recipeDto.getDirections()
        );
    }

    public List<Recipe> getRecipesByCategory(String category) {
        return recipeRepository.findAllByCategoryIgnoreCaseOrderByDateDesc(category);
    }

    public List<Recipe> getRecipesByName(String name) {
        return recipeRepository.findAllByNameContainingIgnoreCaseOrderByDateDesc(name);
    }

    private void updateIngredients(Long recipeId, List<IngredientDto> ingredientDtos) {
        ingredientDtos.forEach(it -> {
            Ingredient ingredient = ingredientsRepository.findByName(it.getName());
            Long id = recipesIngredientsRepository.getIdByRecipeAndIngredient(recipeId, ingredient);
            recipesIngredientsRepository.updateRecipeIngredient(
                    id,
                    recipeId,
                    ingredient,
                    it.getQuantity(),
                    it.getMeasurementUnit()
            );
        });
    }
}
