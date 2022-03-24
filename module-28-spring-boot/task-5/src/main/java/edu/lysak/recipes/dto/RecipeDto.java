package edu.lysak.recipes.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.List;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class RecipeDto {
    @NotBlank(message = "Name should not be blank")
    private String name;

    @NotBlank(message = "Category should not be blank")
    private String category;

    private LocalDateTime date;

    @NotBlank(message = "Description should not be blank")
    private String description;

    @JsonProperty("ingredients")
    private List<IngredientDto> ingredientsDto;

    private String directions;
}
