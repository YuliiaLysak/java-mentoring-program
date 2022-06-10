package edu.lysak.recipes.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ReviewDto {

    private Long reviewId;
    private Long recipeId;

    @Min(value = 0, message = "Rating should not be less than 0")
    @Max(value = 5, message = "Rating should not be more than 5")
    private Integer rating;

    private String comment;

    private String responseMessage;
}
