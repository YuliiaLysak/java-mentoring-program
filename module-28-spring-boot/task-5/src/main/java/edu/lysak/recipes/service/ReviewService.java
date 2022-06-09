package edu.lysak.recipes.service;

import edu.lysak.recipes.dto.ReviewDto;
import edu.lysak.recipes.exception.IllegalActionException;
import edu.lysak.recipes.exception.ReviewNotFoundException;
import edu.lysak.recipes.model.Review;
import edu.lysak.recipes.model.user.User;
import edu.lysak.recipes.repository.ReviewRepository;
import edu.lysak.recipes.service.user.UserService;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.time.LocalDateTime;

@Service
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final UserService userService;

    public ReviewService(ReviewRepository reviewRepository, UserService userService) {
        this.reviewRepository = reviewRepository;
        this.userService = userService;
    }

    public boolean isReviewExist(Long reviewId) {
        return reviewRepository.existsById(reviewId);
    }

    public Long addReview(Long recipeId, ReviewDto reviewDto, Principal principal) {
        User user = userService.getByEmail(principal.getName());
        Review review = createReviewEntity(recipeId, reviewDto, user);
        return reviewRepository.save(review).getReviewId();
    }

    public void deleteReview(Long recipeId, Long reviewId, Principal principal) {
        Review reviewFromDb = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ReviewNotFoundException(String.format("Review with id=%s not found", reviewId)));

        User user = userService.getByEmail(principal.getName());
        if (!user.getId().equals(reviewFromDb.getUserId())) {
            throw new IllegalActionException("You can not delete someone else's review");
        }

        if (!recipeId.equals(reviewFromDb.getRecipeId())) {
            throw new ReviewNotFoundException(String.format("Review with id=%s not found for this recipe", reviewId));
        }

        reviewRepository.deleteById(reviewId);
    }

    private Review createReviewEntity(Long recipeId, ReviewDto reviewDto, User user) {
        return Review.builder()
                .userId(user.getId())
                .recipeId(recipeId)
                .rating(reviewDto.getRating())
                .comment(reviewDto.getComment())
                .date(LocalDateTime.now())
                .build();
    }
}
