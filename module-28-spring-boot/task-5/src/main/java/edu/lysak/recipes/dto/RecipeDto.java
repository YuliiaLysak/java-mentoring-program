package edu.lysak.recipes.dto;

import lombok.Getter;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.List;

@Getter
public class RecipeDto {
    @NotBlank(message = "Name should not be blank")
    private String name;

    @NotBlank(message = "Category should not be blank")
    private String category;

    private LocalDateTime date;

    @NotBlank(message = "Description should not be blank")
    private String description;

    private List<IngredientDto> ingredients;

    private String directions;
}
