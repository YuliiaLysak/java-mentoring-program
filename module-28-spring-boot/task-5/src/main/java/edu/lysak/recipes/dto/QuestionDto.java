package edu.lysak.recipes.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class QuestionDto {

    private Long questionId;
    private Long recipeId;
    private String question;

    private String responseMessage;
}
