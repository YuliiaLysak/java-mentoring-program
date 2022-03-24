package edu.lysak.recipes;

import edu.lysak.recipes.dto.IngredientDto;
import edu.lysak.recipes.dto.RecipeDto;
import edu.lysak.recipes.model.Product;
import edu.lysak.recipes.model.Recipe;
import edu.lysak.recipes.model.Ingredient;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class TestUtil {

    public static Recipe getMockedRecipe(Long id) {
        Recipe mockedRecipe = getMockedRecipe();
        mockedRecipe.setId(id);
        return mockedRecipe;
    }

    public static Recipe getMockedRecipe() {
        return Recipe.builder()
                .name("Warming Ginger Tea")
                .category("beverage")
                .date(LocalDateTime.now())
                .description("description")
                .ingredients(List.of())
                .directions("directions")
                .build();
    }

    public static RecipeDto getMockedRecipeDto() {
        return RecipeDto.builder()
                .name("Warming Ginger Tea")
                .category("beverage")
                .description("description")
                .ingredientsDto(List.of(getIngredientDto()))
                .directions("directions")
                .build();
    }

    public static IngredientDto getIngredientDto() {
        return IngredientDto.builder()
                .name("milk")
                .quantity(200.0)
                .measurementUnit("ml")
                .build();
    }

    public static Product getMockedProduct() {
        return new Product("milk");
    }

    public static List<Ingredient> getMockedIngredients(List<IngredientDto> ingredientsDto) {
        return ingredientsDto.stream()
                .map(it -> Ingredient.builder()
                        .recipeId(1L)
                        .product(getMockedProduct())
                        .quantity(it.getQuantity())
                        .measurementUnit(it.getMeasurementUnit())
                        .build())
                .collect(Collectors.toList());
    }
}
