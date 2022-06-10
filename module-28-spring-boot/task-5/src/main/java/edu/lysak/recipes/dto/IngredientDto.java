package edu.lysak.recipes.dto;

import edu.lysak.recipes.model.NutritionalValue;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class IngredientDto {
    private String name;
    private Double quantity;
    private String measurementUnit;
    private NutritionalValue nutritionalValue;
}
