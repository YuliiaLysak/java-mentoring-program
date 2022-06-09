package edu.lysak.recipes.service;

import edu.lysak.recipes.TestUtil;
import edu.lysak.recipes.dto.IngredientDto;
import edu.lysak.recipes.dto.RecipeDto;
import edu.lysak.recipes.exception.RecipeNotFoundException;
import edu.lysak.recipes.model.Product;
import edu.lysak.recipes.model.Recipe;
import edu.lysak.recipes.repository.RecipeRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.AdditionalAnswers.returnsArgAt;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RecipeServiceTest {

    @InjectMocks
    private RecipeService recipeService;

    @Mock
    private RecipeRepository recipeRepository;
    @Mock
    private IngredientService ingredientService;
    @Mock
    private ProductService productService;

    @Nested
    @DisplayName("#getRecipe(Long)")
    class GetRecipeMethodTest {

        @Test
        @DisplayName("should successfully return recipe")
        void getRecipe_shouldSuccessfullyReturnRecipe() {
            Recipe expectedRecipe = TestUtil.getMockedRecipe();
            when(recipeRepository.findById(any())).thenReturn(Optional.of(expectedRecipe));

            Recipe recipe = recipeService.getRecipe(1L);

            verify(recipeRepository).findById(1L);
            assertEquals(expectedRecipe, recipe);
        }

        @Test
        @DisplayName("should throw RecipeNotFoundException if recipe is not found")
        void getRecipe_shouldThrowExceptionIfRecipeNotFound() {
            when(recipeRepository.findById(any())).thenReturn(Optional.empty());

            assertThrows(RecipeNotFoundException.class,
                    () -> recipeService.getRecipe(1L),
                    "Recipe with id=1 not found");

            verify(recipeRepository).findById(1L);
        }
    }

    @Test
    @DisplayName("#addRecipe(RecipeDto) should successfully save recipe")
    void addRecipe_shouldSuccessfullySaveRecipe() {
        RecipeDto recipeDto = TestUtil.getMockedRecipeDto();
        Product product = TestUtil.getMockedProduct();
        when(recipeRepository.save(any())).thenAnswer(returnsArgAt(0));
        when(productService.saveAndGetProduct(any())).thenReturn(product);
        doNothing().when(ingredientService).saveIngredientIfNotPresent(any(), any(), any());

        Long recipeId = recipeService.addRecipe(recipeDto);

        ArgumentCaptor<Recipe> recipeCaptor = ArgumentCaptor.forClass(Recipe.class);
        verify(recipeRepository).save(recipeCaptor.capture());
        assertEquals(recipeCaptor.getValue().getId(), recipeId);
        assertEquals("test recipe name", recipeCaptor.getValue().getName());
        assertEquals("test category", recipeCaptor.getValue().getCategory());
        assertEquals("test description", recipeCaptor.getValue().getDescription());
        assertEquals("test directions", recipeCaptor.getValue().getDirections());
        verify(productService, times(recipeDto.getIngredientsDto().size())).saveAndGetProduct("milk");

        IngredientDto ingredientDto = recipeDto.getIngredientsDto().get(0);
        verify(ingredientService).saveIngredientIfNotPresent(recipeId, ingredientDto, product);
    }

    @Test
    @DisplayName("#deleteRecipe(Long) should successfully delete recipe")
    void deleteRecipe_shouldSuccessfullyDeleteRecipe() {
        doNothing().when(recipeRepository).deleteRecipe(any());

        recipeService.deleteRecipe(1L);

        verify(recipeRepository).deleteRecipe(1L);
    }

    @Test
    @DisplayName("#updateRecipe(Long, RecipeDto) should successfully update recipe")
    void updateRecipe_shouldSuccessfullyUpdateRecipe() {
        when(productService.saveAndGetProduct(any())).thenReturn(new Product());
        doNothing().when(ingredientService).updateIngredientIfNotPresent(any(), any(), any());
        doNothing().when(recipeRepository).updateRecipe(any(), any(), any(), any(), any(), any());

        RecipeDto recipeDto = TestUtil.getMockedRecipeDto();
        recipeService.updateRecipe(1L, recipeDto);

        ArgumentCaptor<LocalDateTime> timeCaptor = ArgumentCaptor.forClass(LocalDateTime.class);
        verify(recipeRepository).updateRecipe(
                eq(1L),
                eq(recipeDto.getName()),
                eq(recipeDto.getCategory()),
                timeCaptor.capture(),
                eq(recipeDto.getDescription()),
                eq(recipeDto.getDirections())
        );
    }

    @Test
    @DisplayName("#getRecipesByCategory(String) should successfully return list of recipes for particular category")
    void getRecipesByCategory() {
        Recipe recipe = TestUtil.getMockedRecipe();
        when(recipeRepository.findAllByCategoryIgnoreCaseOrderByDateDesc(any())).thenReturn(List.of(recipe));

        List<Recipe> categoryList = recipeService.getRecipesByCategory("beverage");

        verify(recipeRepository).findAllByCategoryIgnoreCaseOrderByDateDesc("beverage");
        assertTrue(categoryList.contains(recipe));
    }

    @Test
    @DisplayName("#getRecipesByName(String) should successfully return list of recipes for particular name")
    void getRecipesByName() {
        Recipe recipe = TestUtil.getMockedRecipe();
        when(recipeRepository.findAllByNameContainingIgnoreCaseOrderByDateDesc(any())).thenReturn(List.of(recipe));

        List<Recipe> nameList = recipeService.getRecipesByName("Warming Ginger Tea");

        verify(recipeRepository).findAllByNameContainingIgnoreCaseOrderByDateDesc("Warming Ginger Tea");
        assertTrue(nameList.contains(recipe));
    }
}
