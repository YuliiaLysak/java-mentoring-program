package edu.lysak.recipes.repository;

import edu.lysak.recipes.model.NutritionalValue;
import edu.lysak.recipes.model.Recipe;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface RecipeRepository extends CrudRepository<Recipe, Long> {

    List<Recipe> findAllByCategoryIgnoreCaseOrderByDateDesc(String category);

    List<Recipe> findAllByNameContainingIgnoreCaseOrderByDateDesc(String name);

    @Transactional
    @Modifying
    @Query("DELETE FROM Recipe r WHERE r.id=:id")
    void deleteRecipe(@Param("id") Long id);

    @Transactional
    @Modifying
    @Query("UPDATE Recipe r SET " +
            "r.name = :name," +
            "r.category = :category," +
            "r.date = :date," +
            "r.description = :description," +
            "r.directions = :directions," +
            "r.nutritionalValue = :nutritionalValue" +
            " WHERE r.id = :id")
    void updateRecipe(
            @Param("id") Long id,
            @Param("name") String name,
            @Param("category") String category,
            @Param("date") LocalDateTime date,
            @Param("description") String description,
            @Param("directions") String directions,
            @Param("nutritionalValue") NutritionalValue nutritionalValue
    );
}
