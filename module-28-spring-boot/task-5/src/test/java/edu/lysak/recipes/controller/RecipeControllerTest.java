package edu.lysak.recipes.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.lysak.recipes.TestUtil;
import edu.lysak.recipes.dto.RecipeDto;
import edu.lysak.recipes.model.Recipe;
import edu.lysak.recipes.service.RecipeService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static edu.lysak.recipes.ResponseBodyMatchers.responseBody;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RecipeController.class)
class RecipeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private RecipeService recipeService;

    @Test
    void getRecipe_shouldSuccessfullyReturnRecipe() throws Exception {
        Recipe expectedRecipe = TestUtil.getMockedRecipe();
        when(recipeService.getRecipe(any())).thenReturn(expectedRecipe);


        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders
                        .get("/api/recipe/{id}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("name").value("Warming Ginger Tea"))
                .andExpect(jsonPath("category").value("beverage"))
//      OR          .andExpect(responseBody().containsObjectAsJson(expectedRecipe, Recipe.class));
                .andReturn();

        assertEquals(mvcResult.getResponse().getContentAsString(), objectMapper.writeValueAsString(expectedRecipe));
    }

    @Test
    void getRecipe_shouldReturn404ifRecipeNotFound() throws Exception {
        when(recipeService.getRecipe(any())).thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/recipe/{id}", 1))
                .andExpect(status().isNotFound());
    }

    @Test
    void addRecipe_shouldAddRecipeAndReturnId() throws Exception {
        when(recipeService.addRecipe(any())).thenReturn(1L);

        RecipeDto recipeDto = TestUtil.getMockedRecipeDto();
        mockMvc.perform(
                        MockMvcRequestBuilders
                                .post("/api/recipe/new")
                                .content(objectMapper.writeValueAsString(recipeDto))
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").value(1L));

        ArgumentCaptor<RecipeDto> recipeCaptor = ArgumentCaptor.forClass(RecipeDto.class);
        verify(recipeService).addRecipe(recipeCaptor.capture());
        assertEquals(recipeCaptor.getValue().getName(), recipeDto.getName());
        assertEquals(recipeCaptor.getValue().getCategory(), recipeDto.getCategory());
        assertEquals(recipeCaptor.getValue().getDirections(), recipeDto.getDirections());
        assertEquals(recipeCaptor.getValue().getDescription(), recipeDto.getDescription());
    }

    // testing @Valid annotation with @NotBlank
    @Test
    void addRecipe_whenNullValue_thenReturns400() throws Exception {
        RecipeDto invalidRecipeDto = RecipeDto.builder()
                .category("beverage")
                .description("description")
                .ingredientsDto(List.of(TestUtil.getIngredientDto()))
                .directions("directions")
                .build();
        mockMvc.perform(
                        MockMvcRequestBuilders
                                .post("/api/recipe/new")
                                .content(objectMapper.writeValueAsString(invalidRecipeDto))
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest())
                .andExpect(responseBody().containsError("name", "Name should not be blank"));

//
//              OR
//
//                .andReturn();
//
//        ErrorResult expectedErrorResponse = new ErrorResult("name", "Name should not be blank");
//        String actualResponseBody = mvcResult.getResponse().getContentAsString();
//        String expectedResponseBody = objectMapper.writeValueAsString(expectedErrorResponse);
//        assertEquals(expectedResponseBody, actualResponseBody);
    }

    @Test
    void deleteRecipe_shouldDeleteRecipeAndReturnNoContentStatus() throws Exception {
        doNothing().when(recipeService).deleteRecipe(any());

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/recipe/{id}", 1))
                .andExpect(status().isNoContent());
    }

    @Test
    void updateRecipe_shouldUpdateRecipeAndReturnNoContentStatus() throws Exception {
        doNothing().when(recipeService).updateRecipe(any(), any());

        mockMvc.perform(MockMvcRequestBuilders
                        .put("/api/recipe/{id}", 1)
                        .content(new ObjectMapper().writeValueAsString(TestUtil.getMockedRecipeDto()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isNoContent());
    }

    @ParameterizedTest
    @CsvSource({"category, beverage", "name, 'Warming Ginger Tea'"})
    void searchRecipes_shouldReturnListOfRecipesWithSpecificRequestParam(String paramName, String paramValue) throws Exception {
        when(recipeService.getRecipesByCategory(any())).thenReturn(List.of(TestUtil.getMockedRecipe()));
        when(recipeService.getRecipesByName(any())).thenReturn(List.of(TestUtil.getMockedRecipe()));

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/api/recipe/search")
                        .param(paramName, paramValue)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].name").value("Warming Ginger Tea"))
                .andExpect(jsonPath("$.[0].category").value("beverage"));
    }

    @Test
    void searchRecipes_shouldThrowExceptionIfNoRequestParam() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/api/recipe/search")
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    void searchRecipes_shouldThrowExceptionIfBothRequestParam() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/api/recipe/search")
                        .param("category", "beverage")
                        .param("name", "tea")
                )
                .andExpect(status().isBadRequest());
    }
}
