package edu.lysak.recipes.repository;

import edu.lysak.recipes.model.Ingredient;
import edu.lysak.recipes.model.RecipesIngredients;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;

public interface RecipesIngredientsRepository extends CrudRepository<RecipesIngredients, Long> {
    @Transactional
    @Modifying
    @Query("UPDATE RecipesIngredients ri SET " +
            "ri.recipeId = :recipe_id," +
            "ri.ingredient = :ingredient," +
            "ri.quantity = :quantity," +
            "ri.measurementUnit = :measurement_unit" +
            " WHERE ri.id = :id")
    void updateRecipeIngredient(
            @Param("id") Long id,
            @Param("recipe_id") Long recipeId,
            @Param("ingredient") Ingredient ingredient,
            @Param("quantity") Double quantity,
            @Param("measurement_unit") String measurementUnit
    );

    @Query("SELECT ri.id FROM RecipesIngredients ri WHERE ri.recipeId = :recipeId AND ri.ingredient = :ingredient")
    Long getIdByRecipeAndIngredient(
            @Param("recipeId") Long recipeId,
            @Param("ingredient") Ingredient ingredient
    );
}
