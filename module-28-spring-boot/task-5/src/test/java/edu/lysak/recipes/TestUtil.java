package edu.lysak.recipes;

import edu.lysak.recipes.dto.IngredientDto;
import edu.lysak.recipes.dto.RecipeDto;
import edu.lysak.recipes.model.NutritionalValue;
import edu.lysak.recipes.model.Product;
import edu.lysak.recipes.model.Recipe;
import edu.lysak.recipes.model.Ingredient;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class TestUtil {

    public static Recipe getMockedRecipe() {
        return Recipe.builder()
                .name("Warming Ginger Tea")
                .category("drink")
                .date(LocalDateTime.now())
                .description("description")
                .ingredients(List.of())
                .directions("directions")
                .nutritionalValue(NutritionalValue.builder()
                        .calories(100)
                        .protein(20)
                        .fat(10)
                        .carbohydrate(20)
                        .build()
                )
                .build();
    }

    public static RecipeDto getMockedRecipeDto() {
        return RecipeDto.builder()
                .name("test recipe name")
                .category("test category")
                .description("test description")
                .ingredientsDto(List.of(getIngredientDto()))
                .directions("test directions")
                .build();
    }

    public static IngredientDto getIngredientDto() {
        return IngredientDto.builder()
                .name("milk")
                .quantity(200.0)
                .measurementUnit("ml")
                .nutritionalValue(NutritionalValue.builder()
                        .calories(100)
                        .protein(20)
                        .fat(10)
                        .carbohydrate(20)
                        .build()
                )
                .build();
    }

    public static Product getMockedProduct() {
        Product product = new Product();
        product.setName("milk");
        product.setNutritionalValue(NutritionalValue.builder()
                .calories(50)
                .protein(30)
                .fat(10)
                .carbohydrate(10)
                .build());
        return product;
    }
}
