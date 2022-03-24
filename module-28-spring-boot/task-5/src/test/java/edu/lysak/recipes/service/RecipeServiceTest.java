package edu.lysak.recipes.service;

import edu.lysak.recipes.TestUtil;
import edu.lysak.recipes.dto.IngredientDto;
import edu.lysak.recipes.dto.RecipeDto;
import edu.lysak.recipes.model.Product;
import edu.lysak.recipes.model.Recipe;
import edu.lysak.recipes.repository.RecipeRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

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

    @Test
    void getRecipe_shouldSuccessfullyReturnRecipe() {
        Recipe expectedRecipe = TestUtil.getMockedRecipe();
        when(recipeRepository.findById(any())).thenReturn(Optional.of(expectedRecipe));

        Recipe recipe = recipeService.getRecipe(1L);

        verify(recipeRepository).findById(1L);
        assertEquals(expectedRecipe, recipe);
    }

    @Test
    void getRecipe_shouldThrowExceptionIfRecipeNotFound() {
        when(recipeRepository.findById(any())).thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class,
                () -> recipeService.getRecipe(1L));

        verify(recipeRepository).findById(1L);
    }

    @Test
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
        assertEquals(recipeCaptor.getValue().getName(), "Warming Ginger Tea");
        assertEquals(recipeCaptor.getValue().getCategory(), "beverage");
        assertEquals(recipeCaptor.getValue().getDescription(), "description");
        assertEquals(recipeCaptor.getValue().getDirections(), "directions");
        verify(productService, times(recipeDto.getIngredientsDto().size())).saveAndGetProduct("milk");

        IngredientDto ingredientDto = recipeDto.getIngredientsDto().get(0);
        verify(ingredientService).saveIngredientIfNotPresent(recipeId, ingredientDto, product);
    }

    @Test
    void deleteRecipe_shouldSuccessfullyDeleteRecipe() {
        doNothing().when(recipeRepository).deleteRecipe(any());

        recipeService.deleteRecipe(1L);

        verify(recipeRepository).deleteRecipe(1L);
    }

    @Test
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
    void getRecipesByCategory() {
        Recipe recipe = TestUtil.getMockedRecipe();
        when(recipeRepository.findAllByCategoryIgnoreCaseOrderByDateDesc(any())).thenReturn(List.of(recipe));

        List<Recipe> categoryList = recipeService.getRecipesByCategory("beverage");

        verify(recipeRepository).findAllByCategoryIgnoreCaseOrderByDateDesc("beverage");
        assertTrue(categoryList.contains(recipe));
    }

    @Test
    void getRecipesByName() {
        Recipe recipe = TestUtil.getMockedRecipe();
        when(recipeRepository.findAllByNameContainingIgnoreCaseOrderByDateDesc(any())).thenReturn(List.of(recipe));

        List<Recipe> nameList = recipeService.getRecipesByName("Warming Ginger Tea");

        verify(recipeRepository).findAllByNameContainingIgnoreCaseOrderByDateDesc("Warming Ginger Tea");
        assertTrue(nameList.contains(recipe));
    }
}
