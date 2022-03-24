package edu.lysak.recipes.repository;

import edu.lysak.recipes.model.Product;
import edu.lysak.recipes.model.Ingredient;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.Optional;

@Repository
public interface IngredientRepository extends CrudRepository<Ingredient, Long> {
    @Transactional
    @Modifying
    @Query("UPDATE Ingredient i SET " +
            "i.recipeId = :recipeId," +
            "i.product = :product," +
            "i.quantity = :quantity," +
            "i.measurementUnit = :measurementUnit" +
            " WHERE i.id = :id")
    void updateIngredient(
            @Param("id") Long id,
            @Param("recipeId") Long recipeId,
            @Param("product") Product product,
            @Param("quantity") Double quantity,
            @Param("measurementUnit") String measurementUnit
    );

    @Query("SELECT i FROM Ingredient i WHERE i.recipeId = :recipeId AND i.product = :product")
    Optional<Ingredient> getIngredientByRecipeIdAndProduct(
            @Param("recipeId") Long recipeId,
            @Param("product") Product product
    );
}
