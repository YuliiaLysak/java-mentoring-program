package edu.lysak.recipes.repository;

import edu.lysak.recipes.TestUtil;
import edu.lysak.recipes.model.Ingredient;
import edu.lysak.recipes.model.Product;
import edu.lysak.recipes.model.Recipe;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class IngredientRepositoryTest {

    @Autowired
    private IngredientRepository ingredientRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private RecipeRepository recipeRepository;

    @Test
    void getIngredientByRecipeIdAndProduct_shouldSuccessfullyFindIngredient() {
        Recipe recipe = TestUtil.getMockedRecipe();
        Long recipeId = recipeRepository.save(recipe).getId();

        Product product = productRepository.save(new Product("milk"));

        ingredientRepository.save(Ingredient.builder()
                .recipeId(recipeId)
                .product(product)
                .quantity(200.0)
                .measurementUnit("ml")
                .build());

        Optional<Ingredient> ingredientFromDb = ingredientRepository.getIngredientByRecipeIdAndProduct(recipeId, product);
        assertTrue(ingredientFromDb.isPresent());
        assertNotNull(ingredientFromDb.get().getId());
        assertEquals(recipeId, ingredientFromDb.get().getRecipeId());
        assertEquals(product, ingredientFromDb.get().getProduct());
        assertEquals(200.0, ingredientFromDb.get().getQuantity());
        assertEquals("ml", ingredientFromDb.get().getMeasurementUnit());
    }

    @Test
    @Sql("classpath:updateIngredient.sql")
    void updateIngredient_shouldSuccessfullyUpdateIngredient() {
        Product product = productRepository.findById(25L).get();
        ingredientRepository.updateIngredient(25L, 25L, product, 400.0, "oz");

        Optional<Ingredient> ingredientFromDb = ingredientRepository.findById(25L);
        assertTrue(ingredientFromDb.isPresent());
        assertNotNull(ingredientFromDb.get().getId());
        assertEquals(400.0, ingredientFromDb.get().getQuantity());
        assertEquals("oz", ingredientFromDb.get().getMeasurementUnit());
    }
}
