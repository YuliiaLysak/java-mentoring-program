package edu.lysak.recipes.controller;

import edu.lysak.recipes.dto.ReviewDto;
import edu.lysak.recipes.service.RecipeService;
import edu.lysak.recipes.service.ReviewService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.security.Principal;

@RestController
public class ReviewController {
    private final ReviewService reviewService;
    private final RecipeService recipeService;

    public ReviewController(ReviewService reviewService, RecipeService recipeService) {
        this.reviewService = reviewService;
        this.recipeService = recipeService;
    }

    @PostMapping("/api/recipe/{recipeId}/review")
    public ResponseEntity<ReviewDto> addReview(
            @PathVariable("recipeId") Long recipeId,
            @Valid @RequestBody ReviewDto reviewDto,
            Principal principal
    ) {
        if (!recipeService.isRecipeExist(recipeId)) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(prepareResponse(recipeId, null, "Recipe not found"));
        }

        Long reviewId = reviewService.addReview(recipeId, reviewDto, principal);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(prepareResponse(recipeId, reviewId, "Thank you for the review"));
    }

    @DeleteMapping("/api/recipe/{recipeId}/review/{reviewId}")
    public ResponseEntity<ReviewDto> deleteReview(
            @PathVariable("recipeId") Long recipeId,
            @PathVariable("reviewId") Long reviewId,
            Principal principal
    ) {

        if (!recipeService.isRecipeExist(recipeId)) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(prepareResponse(recipeId, reviewId, "Recipe not found"));
        }

        if (!reviewService.isReviewExist(reviewId)) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(prepareResponse(recipeId, reviewId, "Review not found"));
        }

        reviewService.deleteReview(recipeId, reviewId, principal);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(prepareResponse(recipeId, reviewId, "Review was successfully deleted"));
    }

    private ReviewDto prepareResponse(Long recipeId, Long reviewId, String message) {
        ReviewDto response = new ReviewDto();
        response.setReviewId(reviewId);
        response.setRecipeId(recipeId);
        response.setResponseMessage(message);
        return response;
    }
}
