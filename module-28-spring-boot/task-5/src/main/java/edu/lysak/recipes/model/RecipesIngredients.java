package edu.lysak.recipes.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "recipes_ingredients")
public class RecipesIngredients {
    @JsonIgnore
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(name = "recipe_id")
    private Long recipeId;

    @NotNull
    @OneToOne(cascade = {CascadeType.ALL})
    @JoinColumn(name = "ingredient_id", referencedColumnName = "id")
    private Ingredient ingredient;

    @NotNull
    @Column(name = "quantity")
    private Double quantity;

    @NotNull
    @Column(name = "measurement_unit")
    private String measurementUnit;
}
