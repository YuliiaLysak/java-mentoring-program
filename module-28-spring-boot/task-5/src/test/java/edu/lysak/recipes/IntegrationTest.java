package edu.lysak.recipes;

import edu.lysak.recipes.dto.RecipeDto;
import edu.lysak.recipes.model.Ingredient;
import edu.lysak.recipes.model.Product;
import edu.lysak.recipes.model.Recipe;
import edu.lysak.recipes.repository.IngredientRepository;
import edu.lysak.recipes.repository.ProductRepository;
import edu.lysak.recipes.repository.RecipeRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
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

    @Nested
    @DisplayName("#getRecipe(Long)")
    class GetRecipeMethodTest {

        @Test
        @DisplayName("should successfully get recipe and return status 200 (OK)")
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
        @DisplayName("should not get recipe and return status 401 (unauthorized) if user is unauthenticated")
        void getRecipe_ifUserCredentialsNotProvided_shouldReturnUnauthorizedStatus() {
            ResponseEntity<Recipe> recipe = restTemplate
                    // used id=1000 -> see migration script V1001__Add_test_recipe.sql
                    .getForEntity("/api/recipe/1000", Recipe.class);

            assertEquals(HttpStatus.UNAUTHORIZED, recipe.getStatusCode());
        }
    }

    @Nested
    @DisplayName("#addRecipe(RecipeDto)")
    class AddRecipeMethodTest {

        @Test
        @DisplayName("should successfully add new recipe and return it's id and status 200")
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

        @Test
        @DisplayName("should not add new recipe and return status 401 (unauthorized) if user is unauthenticated")
        void addRecipe_shouldNotAddRecipe_andReturnStatus401() {
            RecipeDto recipeDto = TestUtil.getMockedRecipeDto();
            ResponseEntity<Map> response = restTemplate
                    .postForEntity("/api/recipe/new", recipeDto, Map.class);

            assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
            Map body = response.getBody();
            assertNull(body);
        }
    }

    @Nested
    @DisplayName("#deleteRecipe(Long)")
    class DeleteRecipeMethodTest {

        @Test
        @DisplayName("should successfully delete recipe and return status 204 (no content) if user is authorized (has role 'ADMIN')")
        void deleteRecipe_shouldSuccessfullyDeleteRecipe() {
            ResponseEntity<Void> response = restTemplate
                    .withBasicAuth("admin@com.ua", "admin")
                    .exchange("/api/recipe/{id}", HttpMethod.DELETE, HttpEntity.EMPTY, Void.class, 1);

            assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());

            Optional<Recipe> recipe = recipeRepository.findById(1L);
            assertTrue(recipe.isEmpty());
        }

        @Test
        @DisplayName("should not delete recipe and return status 403 (forbidden) if user is unauthorized (does not have role 'ADMIN')")
        void deleteRecipe_shouldNotDeleteRecipe() {
            ResponseEntity<Void> response = restTemplate
                    .withBasicAuth("user@com.ua", "user")
                    .exchange("/api/recipe/{id}", HttpMethod.DELETE, HttpEntity.EMPTY, Void.class, 2);

            assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());

            Optional<Recipe> recipe = recipeRepository.findById(2L);
            assertTrue(recipe.isPresent());
        }

        @Test
        @DisplayName("should not delete recipe and return status 401 (unauthorized) if user is unauthenticated")
        void deleteRecipe_shouldNotDeleteRecipe_andReturnStatus401() {
            ResponseEntity<Void> response = restTemplate
                    .exchange("/api/recipe/{id}", HttpMethod.DELETE, HttpEntity.EMPTY, Void.class, 2);

            assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());

            Optional<Recipe> recipe = recipeRepository.findById(2L);
            assertTrue(recipe.isPresent());
        }
    }
}
