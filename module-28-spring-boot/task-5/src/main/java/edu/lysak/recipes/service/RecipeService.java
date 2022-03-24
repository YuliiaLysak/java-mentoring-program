package edu.lysak.recipes.service;

import edu.lysak.recipes.dto.IngredientDto;
import edu.lysak.recipes.dto.RecipeDto;
import edu.lysak.recipes.model.Product;
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

        recipeDto.getIngredientsDto().forEach(ingredientDto -> {
            Product product = productService.saveAndGetProduct(ingredientDto.getName());
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
        ingredientDtos.forEach(ingredientDto -> {
            Product product = productService.saveAndGetProduct(ingredientDto.getName());
            ingredientService.updateIngredientIfNotPresent(recipeId, ingredientDto, product);
        });
    }
}
