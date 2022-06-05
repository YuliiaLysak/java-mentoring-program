package edu.lysak.recipes;

import edu.lysak.recipes.dto.RecipeDto;
import edu.lysak.recipes.model.Ingredient;
import edu.lysak.recipes.model.Product;
import edu.lysak.recipes.model.Recipe;
import edu.lysak.recipes.repository.IngredientRepository;
import edu.lysak.recipes.repository.ProductRepository;
import edu.lysak.recipes.repository.RecipeRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class IntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private RecipeRepository recipeRepository;

    @Autowired
    private IngredientRepository ingredientRepository;

    @Autowired
    private ProductRepository productRepository;

    @Test
    void getRecipe_shouldSuccessfullyReturnRecipe() {
        ResponseEntity<Recipe> recipe = restTemplate
                .withBasicAuth("user@com.ua", "user")
                // used id=1000 -> see migration script V1001__Add_test_recipe.sql
                .getForEntity("/api/recipe/1000", Recipe.class);

        assertEquals(HttpStatus.OK, recipe.getStatusCode());

        assertNotNull(recipe.getBody());
        assertEquals("name", recipe.getBody().getName());
        assertEquals("category", recipe.getBody().getCategory());
        assertEquals("description", recipe.getBody().getDescription());
        assertEquals("directions", recipe.getBody().getDirections());
    }

    @Test
    void getRecipe_ifUserCredentialsNotProvided_shouldReturnUnauthorizedStatus() {
        ResponseEntity<Recipe> recipe = restTemplate
                // used id=1000 -> see migration script V1001__Add_test_recipe.sql
                .getForEntity("/api/recipe/1000", Recipe.class);

        assertEquals(HttpStatus.UNAUTHORIZED, recipe.getStatusCode());
    }

    @Test
    void addRecipe_shouldSuccessfullyAddRecipe() {
        RecipeDto recipeDto = TestUtil.getMockedRecipeDto();
        ResponseEntity<Map> response = restTemplate
                .withBasicAuth("user@com.ua", "user")
                .postForEntity("/api/recipe/new", recipeDto, Map.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        Map body = response.getBody();
        assertNotNull(body);
        // used id=3 because 2 recipes was added by migration in src/main/resources/db/migration
        // and 1 recipe was added by migration in src/test/resources/db/migration but with id=1000
        assertEquals(3, body.get("id"));

        Optional<Recipe> recipe = recipeRepository.findById(3L);
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
