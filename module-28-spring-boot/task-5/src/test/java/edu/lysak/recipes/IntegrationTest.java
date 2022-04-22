package edu.lysak.recipes;

import edu.lysak.recipes.dto.RecipeDto;
import edu.lysak.recipes.model.Ingredient;
import edu.lysak.recipes.model.Product;
import edu.lysak.recipes.model.Recipe;
import edu.lysak.recipes.repository.IngredientRepository;
import edu.lysak.recipes.repository.ProductRepository;
import edu.lysak.recipes.repository.RecipeRepository;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class IntegrationTest {

    // TODO: 06.04.2022 fix tests after adding spring security

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private RecipeRepository recipeRepository;

    @Autowired
    private IngredientRepository ingredientRepository;

    @Autowired
    private ProductRepository productRepository;

    @Test
    @Order(1)
    void getRecipe_shouldSuccessfullyReturnRecipe() {
        RecipeDto recipeDto = TestUtil.getMockedRecipeDto();
        restTemplate.postForEntity("/api/recipe/new", recipeDto, Map.class);
        ResponseEntity<Recipe> recipe = restTemplate.getForEntity("/api/recipe/1", Recipe.class);

        assertEquals(HttpStatus.OK, recipe.getStatusCode());

        assertNotNull(recipe.getBody());
        assertEquals(recipeDto.getName(), recipe.getBody().getName());
        assertEquals(recipeDto.getCategory(), recipe.getBody().getCategory());
        assertEquals(recipeDto.getDescription(), recipe.getBody().getDescription());
        assertEquals(recipeDto.getDirections(), recipe.getBody().getDirections());
    }

    @Test
    @Order(2)
    void addRecipe_shouldSuccessfullyAddRecipe() {
        RecipeDto recipeDto = TestUtil.getMockedRecipeDto();
        ResponseEntity<Map> response = restTemplate.postForEntity("/api/recipe/new", recipeDto, Map.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        Map body = response.getBody();
        assertNotNull(body);
        assertEquals(2, body.get("id"));

        Optional<Recipe> recipe = recipeRepository.findById(2L);
        assertTrue(recipe.isPresent());
        assertEquals(recipeDto.getName(), recipe.get().getName());
        assertEquals(recipeDto.getCategory(), recipe.get().getCategory());
        assertEquals(recipeDto.getDescription(), recipe.get().getDescription());
        assertEquals(recipeDto.getDirections(), recipe.get().getDirections());

        Optional<Product> product = productRepository.findByName("milk");
        assertTrue(product.isPresent());
        assertNotNull(product.get().getId());

        Optional<Ingredient> ingredient = ingredientRepository.getIngredientByRecipeIdAndProduct(recipe.get().getId(), product.get());
        assertTrue(ingredient.isPresent());
        assertNotNull(ingredient.get().getId());
        assertEquals(recipe.get().getId(), ingredient.get().getRecipeId());
        assertEquals(product.get().getId(), ingredient.get().getProduct().getId());
    }
}
