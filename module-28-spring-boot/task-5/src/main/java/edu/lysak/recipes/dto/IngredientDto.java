package edu.lysak.recipes.dto;

import lombok.Getter;

@Getter
public class IngredientDto {
    private String name;
    private Double quantity;
    private String measurementUnit;
}
