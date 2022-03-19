package edu.lysak.recipes.repository;

import edu.lysak.recipes.model.Ingredient;
import org.springframework.data.repository.CrudRepository;

public interface IngredientRepository extends CrudRepository<Ingredient, Long> {

    Ingredient findByName(String name);
}
