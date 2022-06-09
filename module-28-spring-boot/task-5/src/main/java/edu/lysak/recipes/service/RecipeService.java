package edu.lysak.recipes.service;

import edu.lysak.recipes.dto.IngredientDto;
import edu.lysak.recipes.dto.RecipeDto;
import edu.lysak.recipes.exception.RecipeNotFoundException;
import edu.lysak.recipes.model.NutritionalValue;
import edu.lysak.recipes.model.Product;
import edu.lysak.recipes.model.Recipe;
import edu.lysak.recipes.repository.RecipeRepository;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class RecipeService {
    private final RecipeRepository recipeRepository;
    private final IngredientService ingredientService;
    private final ProductService productService;

    public RecipeService(
            RecipeRepository recipeRepository,
            IngredientService ingredientService,
            ProductService productService
    ) {
        this.recipeRepository = recipeRepository;
        this.ingredientService = ingredientService;
        this.productService = productService;
    }

    public boolean isRecipeExist(Long recipeId) {
        return recipeRepository.existsById(recipeId);
    }

    public Recipe getRecipe(Long id) {
        return recipeRepository.findById(id)
                .orElseThrow(() -> new RecipeNotFoundException(String.format("Recipe with id=%s not found", id)));
    }

    @Transactional
    public Long addRecipe(RecipeDto recipeDto) {
        Recipe recipe = Recipe.builder()
                .name(recipeDto.getName())
                .category(recipeDto.getCategory())
                .date(LocalDateTime.now())
                .description(recipeDto.getDescription())
                .directions(recipeDto.getDirections())
                .nutritionalValue(NutritionalValue.builder()
                        .calories(getTotalCalories(recipeDto.getIngredientsDto()))
                        .protein(getTotalProtein(recipeDto.getIngredientsDto()))
                        .fat(getTotalFat(recipeDto.getIngredientsDto()))
                        .carbohydrate(getTotalCarbohydrate(recipeDto.getIngredientsDto()))
                        .build())
                .build();
        Long recipeId = recipeRepository.save(recipe).getId();

        recipeDto.getIngredientsDto().forEach(ingredientDto -> {
            Product product = productService.saveAndGetProduct(ingredientDto);
            ingredientService.saveIngredientIfNotPresent(recipeId, ingredientDto, product);
        });

        return recipeId;
    }

    public void deleteRecipe(Long recipeId) {
        recipeRepository.deleteRecipe(recipeId);
    }

    public void updateRecipe(Long recipeId, RecipeDto recipeDto) {
        updateIngredients(recipeId, recipeDto.getIngredientsDto());
        recipeRepository.updateRecipe(
                recipeId,
                recipeDto.getName(),
                recipeDto.getCategory(),
                LocalDateTime.now(),
                recipeDto.getDescription(),
                recipeDto.getDirections(),
                NutritionalValue.builder()
                        .calories(getTotalCalories(recipeDto.getIngredientsDto()))
                        .protein(getTotalProtein(recipeDto.getIngredientsDto()))
                        .fat(getTotalFat(recipeDto.getIngredientsDto()))
                        .carbohydrate(getTotalCarbohydrate(recipeDto.getIngredientsDto()))
                        .build()
        );
    }

    public List<Recipe> getRecipesByCategory(String category) {
        return recipeRepository.findAllByCategoryIgnoreCaseOrderByDateDesc(category);
    }

    public List<Recipe> getRecipesByName(String name) {
        return recipeRepository.findAllByNameContainingIgnoreCaseOrderByDateDesc(name);
    }

    private void updateIngredients(Long recipeId, List<IngredientDto> ingredientDtos) {
        ingredientDtos.forEach(ingredientDto -> {
            Product product = productService.saveAndGetProduct(ingredientDto);
            ingredientService.updateIngredientIfNotPresent(recipeId, ingredientDto, product);
        });
    }

    private Integer getTotalCalories(List<IngredientDto> ingredientsDto) {
        return ingredientsDto.stream()
                .map(IngredientDto::getNutritionalValue)
                .map(NutritionalValue::getCalories)
                .mapToInt(it -> it)
                .sum();
    }

    private Integer getTotalProtein(List<IngredientDto> ingredientsDto) {
        return ingredientsDto.stream()
                .map(IngredientDto::getNutritionalValue)
                .map(NutritionalValue::getProtein)
                .mapToInt(it -> it)
                .sum();
    }

    private Integer getTotalFat(List<IngredientDto> ingredientsDto) {
        return ingredientsDto.stream()
                .map(IngredientDto::getNutritionalValue)
                .map(NutritionalValue::getFat)
                .mapToInt(it -> it)
                .sum();
    }

    private Integer getTotalCarbohydrate(List<IngredientDto> ingredientsDto) {
        return ingredientsDto.stream()
                .map(IngredientDto::getNutritionalValue)
                .map(NutritionalValue::getCarbohydrate)
                .mapToInt(it -> it)
                .sum();
    }
}
