package edu.lysak.recipes.service;

import edu.lysak.recipes.TestUtil;
import edu.lysak.recipes.dto.IngredientDto;
import edu.lysak.recipes.model.Ingredient;
import edu.lysak.recipes.model.Product;
import edu.lysak.recipes.repository.IngredientRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.AdditionalAnswers.returnsArgAt;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class IngredientServiceTest {

    @InjectMocks
    private IngredientService ingredientService;

    @Mock
    private IngredientRepository ingredientRepository;

    @Test
    void saveIngredientIfNotPresent_shouldNotSaveIngredient() {
        Product product = new Product("milk");
        IngredientDto ingredientDto = TestUtil.getIngredientDto();
        when(ingredientRepository.getIngredientByRecipeIdAndProduct(any(), any())).thenReturn(Optional.of(Ingredient.builder().build()));

        ingredientService.saveIngredientIfNotPresent(1L, ingredientDto, product);

        verify(ingredientRepository).getIngredientByRecipeIdAndProduct(1L, product);
        verify(ingredientRepository, never()).save(any());
    }

    @Test
    void saveIngredientIfNotPresent_shouldSuccessfullySaveIngredient() {
        Product product = new Product(1L, "milk");
        IngredientDto ingredientDto = TestUtil.getIngredientDto();

        when(ingredientRepository.getIngredientByRecipeIdAndProduct(any(), any())).thenReturn(Optional.empty());
        when(ingredientRepository.save(any())).thenAnswer(returnsArgAt(0));

        ingredientService.saveIngredientIfNotPresent(1L, ingredientDto, product);

        verify(ingredientRepository).getIngredientByRecipeIdAndProduct(1L, product);
        ArgumentCaptor<Ingredient> ingredientCaptor = ArgumentCaptor.forClass(Ingredient.class);
        verify(ingredientRepository).save(ingredientCaptor.capture());

        Ingredient savedIngredient = ingredientCaptor.getValue();
        assertEquals(1L, savedIngredient.getRecipeId());
        assertEquals(1L, savedIngredient.getProduct().getId());
        assertEquals(200.0, savedIngredient.getQuantity());
        assertEquals("ml", savedIngredient.getMeasurementUnit());
    }

    @Test
    void updateIngredientIfNotPresent_shouldSaveNewIngredient() {
        Product product = new Product(1L, "milk");
        IngredientDto ingredientDto = TestUtil.getIngredientDto();

        when(ingredientRepository.getIngredientByRecipeIdAndProduct(any(), any())).thenReturn(Optional.empty());
        when(ingredientRepository.save(any())).thenAnswer(returnsArgAt(0));

        ingredientService.updateIngredientIfNotPresent(1L, ingredientDto, product);

        verify(ingredientRepository).getIngredientByRecipeIdAndProduct(1L, product);
        verify(ingredientRepository, never()).updateIngredient(any(), any(), any(), any(), any());
        ArgumentCaptor<Ingredient> ingredientCaptor = ArgumentCaptor.forClass(Ingredient.class);
        verify(ingredientRepository).save(ingredientCaptor.capture());
        Ingredient savedIngredient = ingredientCaptor.getValue();
        assertEquals(1L, savedIngredient.getRecipeId());
        assertEquals(1L, savedIngredient.getProduct().getId());
        assertEquals(200.0, savedIngredient.getQuantity());
        assertEquals("ml", savedIngredient.getMeasurementUnit());
    }

    @Test
    void updateIngredientIfNotPresent_shouldUpdateExistingIngredient() {
        Product product = new Product("milk");
        IngredientDto ingredientDto = TestUtil.getIngredientDto();
        Ingredient ingredient = Ingredient.builder()
                .recipeId(1L)
                .product(product)
                .quantity(200.0)
                .measurementUnit("ml")
                .build();

        when(ingredientRepository.getIngredientByRecipeIdAndProduct(any(), any()))
                .thenReturn(Optional.of(ingredient));
        doNothing().when(ingredientRepository).updateIngredient(any(), any(), any(), any(), any());

        ingredientService.updateIngredientIfNotPresent(1L, ingredientDto, product);

        verify(ingredientRepository).getIngredientByRecipeIdAndProduct(1L, product);
        verify(ingredientRepository).updateIngredient(
                ingredient.getId(),
                1L,
                product,
                ingredientDto.getQuantity(),
                ingredientDto.getMeasurementUnit()
        );
        verify(ingredientRepository, never()).save(any());
    }
}
