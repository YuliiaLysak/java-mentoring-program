package edu.lysak.recipes.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.lysak.recipes.TestUtil;
import edu.lysak.recipes.dto.RecipeDto;
import edu.lysak.recipes.model.Recipe;
import edu.lysak.recipes.service.RecipeService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static edu.lysak.recipes.ResponseBodyMatchers.responseBody;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RecipeController.class)
@ContextConfiguration(classes = {
        ControllerExceptionHandler.class,
        RecipeController.class,
})
class RecipeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private RecipeService recipeService;

    @Nested
    @DisplayName("#getRecipe(Long)")
    class GetRecipeMethodTest {

        @WithMockUser
        @Test
        @DisplayName("should successfully return recipe and status 200")
        void getRecipe_shouldSuccessfullyReturnRecipe() throws Exception {
            Recipe expectedRecipe = TestUtil.getMockedRecipe();
            when(recipeService.getRecipe(any())).thenReturn(expectedRecipe);


            MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders
                            .get("/api/recipe/{id}", 1)
                            .contentType(MediaType.APPLICATION_JSON)
                    )
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("name").value("Warming Ginger Tea"))
                    .andExpect(jsonPath("category").value("drink"))
//      OR          .andExpect(responseBody().containsObjectAsJson(expectedRecipe, Recipe.class));
                    .andReturn();

            assertEquals(mvcResult.getResponse().getContentAsString(), objectMapper.writeValueAsString(expectedRecipe));
        }

        @WithMockUser
        @Test
        @DisplayName("should return status 404 (not found) if recipe not found")
        void getRecipe_shouldReturn404ifRecipeNotFound() throws Exception {
            when(recipeService.getRecipe(any())).thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND));

            mockMvc.perform(MockMvcRequestBuilders.get("/api/recipe/{id}", 1))
                    .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("should return status 401 (unauthorized) if user is unauthenticated")
        void getRecipe_shouldReturn401ifUserUnauthorized() throws Exception {
            mockMvc.perform(MockMvcRequestBuilders
                            .get("/api/recipe/{id}", 1)
                            .contentType(MediaType.APPLICATION_JSON)
                    )
                    .andExpect(status().isUnauthorized());
        }
    }

    @Nested
    @DisplayName("#addRecipe(RecipeDto)")
    class AddRecipeMethodTest {

        @WithMockUser
        @Test
        @DisplayName("should successfully add new recipe and return it's id and status 200")
        void addRecipe_shouldAddRecipeAndReturnId() throws Exception {
            when(recipeService.addRecipe(any())).thenReturn(1L);

            RecipeDto recipeDto = TestUtil.getMockedRecipeDto();
            mockMvc.perform(
                            MockMvcRequestBuilders
                                    .post("/api/recipe/new")
                                    .with(csrf())
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
        @WithMockUser
        @Test
        @DisplayName("should return status 400 (bad request) if dto is invalid")
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
                                    .with(csrf())
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
        @DisplayName("should not add new recipe and return status 401 (unauthorized) if user is unauthenticated")
        void addRecipe_shouldReturn401ifUserUnauthorized() throws Exception {
            when(recipeService.addRecipe(any())).thenReturn(1L);

            RecipeDto recipeDto = TestUtil.getMockedRecipeDto();
            mockMvc.perform(
                            MockMvcRequestBuilders
                                    .post("/api/recipe/new")
                                    .with(csrf())
                                    .content(objectMapper.writeValueAsString(recipeDto))
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .accept(MediaType.APPLICATION_JSON)
                    )
                    .andExpect(status().isUnauthorized());

            verify(recipeService, never()).addRecipe(any());
        }
    }

    @Nested
    @DisplayName("#deleteRecipe(Long)")
    class DeleteRecipeMethodTest {

        // Successful deletion of recipe (204, no content)
        // and failed deletion of recipe (403, forbidden)
        // are tested in IntegrationTest.class

        @Test
        @DisplayName("should not delete recipe and return status 401 (unauthorized) if user is unauthenticated")
        void deleteRecipe_shouldNotDeleteRecipeAndReturn401Status() throws Exception {
            mockMvc.perform(
                            MockMvcRequestBuilders
                                    .delete("/api/recipe/{id}", 1)
                                    .with(csrf())
                    )
                    .andExpect(status().isUnauthorized());

            verify(recipeService, never()).deleteRecipe(any());
        }
    }

    @Nested
    @DisplayName("#updateRecipe(Long, RecipeDto)")
    class UpdateRecipeMethodTest {

        @WithMockUser
        @Test
        @DisplayName("should successfully update recipe and return status 204 (no content)")
        void updateRecipe_shouldUpdateRecipeAndReturnNoContentStatus() throws Exception {
            RecipeDto recipeDto = TestUtil.getMockedRecipeDto();

            mockMvc.perform(MockMvcRequestBuilders
                            .put("/api/recipe/{id}", 1)
                            .with(csrf())
                            .content(new ObjectMapper().writeValueAsString(recipeDto))
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON)
                    )
                    .andExpect(status().isNoContent());

            ArgumentCaptor<RecipeDto> recipeCaptor = ArgumentCaptor.forClass(RecipeDto.class);
            verify(recipeService).updateRecipe(eq(1L), recipeCaptor.capture());
            assertEquals(recipeDto.getName(), recipeCaptor.getValue().getName());
            assertEquals(recipeDto.getCategory(), recipeCaptor.getValue().getCategory());
            assertEquals(recipeDto.getDirections(), recipeCaptor.getValue().getDirections());
            assertEquals(recipeDto.getDescription(), recipeCaptor.getValue().getDescription());
        }

        @Test
        @DisplayName("should not update recipe and return status 401 (unauthorized) if user is unauthenticated")
        void updateRecipe_shouldNotUpdateRecipeAndReturn401Status() throws Exception {
            mockMvc.perform(MockMvcRequestBuilders
                            .put("/api/recipe/{id}", 1)
                            .with(csrf())
                            .content(new ObjectMapper().writeValueAsString(TestUtil.getMockedRecipeDto()))
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON)
                    )
                    .andExpect(status().isUnauthorized());

            verify(recipeService, never()).updateRecipe(any(), any());
        }
    }

    @Nested
    @DisplayName("#searchRecipes(String, String)")
    class SearchRecipesMethodTest {

        @WithMockUser
        @ParameterizedTest
        @CsvSource({"category, drink", "name, 'Warming Ginger Tea'"})
        @DisplayName("should successfully return list of recipes for particular category/name")
        void searchRecipes_shouldReturnListOfRecipesWithSpecificRequestParam(String paramName, String paramValue) throws Exception {
            when(recipeService.getRecipesByCategory(any())).thenReturn(List.of(TestUtil.getMockedRecipe()));
            when(recipeService.getRecipesByName(any())).thenReturn(List.of(TestUtil.getMockedRecipe()));

            mockMvc.perform(MockMvcRequestBuilders
                            .get("/api/recipe/search")
                            .param(paramName, paramValue)
                    )
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.[0].name").value("Warming Ginger Tea"))
                    .andExpect(jsonPath("$.[0].category").value("drink"));
        }

        @WithMockUser
        @Test
        @DisplayName("should return 400 (bad request) if no request parameters are provided")
        void searchRecipes_shouldThrowExceptionIfNoRequestParam() throws Exception {

            mockMvc.perform(MockMvcRequestBuilders
                            .get("/api/recipe/search")
                    )
                    .andExpect(status().isBadRequest());
        }

        @WithMockUser
        @Test
        @DisplayName("should return 400 (bad request) if both request parameters are provided")
        void searchRecipes_shouldThrowExceptionIfBothRequestParam() throws Exception {

            mockMvc.perform(MockMvcRequestBuilders
                            .get("/api/recipe/search")
                            .param("category", "beverage")
                            .param("name", "tea")
                    )
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("should return status 401 (unauthorized) if user is unauthenticated")
        void searchRecipes_shouldReturnStatus401() throws Exception {

            mockMvc.perform(MockMvcRequestBuilders
                            .get("/api/recipe/search")
                            .param("category", "beverage")
                    )
                    .andExpect(status().isUnauthorized());
        }
    }
}
