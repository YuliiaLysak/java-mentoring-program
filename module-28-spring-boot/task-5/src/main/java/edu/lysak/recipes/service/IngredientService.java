package edu.lysak.recipes.service;

import edu.lysak.recipes.dto.IngredientDto;
import edu.lysak.recipes.model.Ingredient;
import edu.lysak.recipes.model.Product;
import edu.lysak.recipes.repository.IngredientRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class IngredientService {
    private final IngredientRepository ingredientRepository;

    public IngredientService(IngredientRepository ingredientRepository) {
        this.ingredientRepository = ingredientRepository;
    }

    public void saveIngredientIfNotPresent(Long recipeId, IngredientDto ingredientDto, Product product) {
        if (ingredientRepository.getIngredientByRecipeIdAndProduct(recipeId, product).isPresent()) {
            return;
        }
        ingredientRepository.save(Ingredient.builder()
                .recipeId(recipeId)
                .product(product)
                .quantity(ingredientDto.getQuantity())
                .measurementUnit(ingredientDto.getMeasurementUnit())
                .build());
    }

    public void updateIngredientIfNotPresent(Long recipeId, IngredientDto ingredientDto, Product product) {
        Optional<Ingredient> opt = ingredientRepository.getIngredientByRecipeIdAndProduct(recipeId, product);
        if (opt.isPresent()) {
            ingredientRepository.updateIngredient(
                    opt.get().getId(),
                    recipeId,
                    product,
                    ingredientDto.getQuantity(),
                    ingredientDto.getMeasurementUnit()
            );
        } else {
            ingredientRepository.save(Ingredient.builder()
                    .recipeId(recipeId)
                    .product(product)
                    .quantity(ingredientDto.getQuantity())
                    .measurementUnit(ingredientDto.getMeasurementUnit())
                    .build());
        }
    }
}
