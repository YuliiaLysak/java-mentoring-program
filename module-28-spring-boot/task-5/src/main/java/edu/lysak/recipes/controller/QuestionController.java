package edu.lysak.recipes.controller;

import edu.lysak.recipes.dto.QuestionDto;
import edu.lysak.recipes.service.QuestionService;
import edu.lysak.recipes.service.RecipeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

import static org.springframework.http.ResponseEntity.status;

@RestController
public class QuestionController {
    private final QuestionService questionService;
    private final RecipeService recipeService;

    public QuestionController(QuestionService questionService, RecipeService recipeService) {
        this.questionService = questionService;
        this.recipeService = recipeService;
    }

    @PostMapping("/api/recipe/{recipeId}/question")
    public ResponseEntity<QuestionDto> addQuestion(
            @PathVariable("recipeId") Long recipeId,
            @RequestBody QuestionDto questionDto,
            Principal principal
    ) {

        if (!recipeService.isRecipeExist(recipeId)) {
            return status(HttpStatus.NOT_FOUND)
                    .body(prepareResponse(recipeId, null, "Recipe not found"));
        }

        Long questionId = questionService.addQuestion(recipeId, questionDto, principal);
        return status(HttpStatus.OK)
                .body(prepareResponse(recipeId, questionId, "Question was successfully added"));
    }

    @DeleteMapping("/api/recipe/{recipeId}/question/{questionId}")
    public ResponseEntity<QuestionDto> deleteQuestion(
            @PathVariable("recipeId") Long recipeId,
            @PathVariable("questionId") Long questionId,
            Principal principal
    ) {

        if (!recipeService.isRecipeExist(recipeId)) {
            return status(HttpStatus.NOT_FOUND)
                    .body(prepareResponse(recipeId, questionId, "Recipe not found"));
        }

        if (!questionService.isQuestionExist(questionId)) {
            return status(HttpStatus.NOT_FOUND)
                    .body(prepareResponse(recipeId, questionId, "Question not found"));
        }

        questionService.deleteQuestion(recipeId, questionId, principal);
        return status(HttpStatus.OK)
                .body(prepareResponse(recipeId, questionId, "Question was successfully deleted"));
    }

    private QuestionDto prepareResponse(Long recipeId, Long questionId, String message) {
        QuestionDto response = new QuestionDto();
        response.setQuestionId(questionId);
        response.setRecipeId(recipeId);
        response.setResponseMessage(message);
        return response;
    }
}
