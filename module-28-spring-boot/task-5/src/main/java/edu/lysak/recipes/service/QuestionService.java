package edu.lysak.recipes.service;

import edu.lysak.recipes.dto.QuestionDto;
import edu.lysak.recipes.exception.IllegalActionException;
import edu.lysak.recipes.exception.QuestionNotFoundException;
import edu.lysak.recipes.model.Question;
import edu.lysak.recipes.model.user.User;
import edu.lysak.recipes.repository.QuestionRepository;
import edu.lysak.recipes.service.user.UserService;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.time.LocalDateTime;

@Service
public class QuestionService {

    private final QuestionRepository questionRepository;
    private final UserService userService;

    public QuestionService(QuestionRepository questionRepository, UserService userService) {
        this.questionRepository = questionRepository;
        this.userService = userService;
    }

    public boolean isQuestionExist(Long questionId) {
        return questionRepository.existsById(questionId);
    }

    public Long addQuestion(Long recipeId, QuestionDto questionDto, Principal principal) {
        User user = userService.getByEmail(principal.getName());
        Question question = createQuestionEntity(recipeId, questionDto, user);
        return questionRepository.save(question).getQuestionId();
    }

    public void deleteQuestion(Long recipeId, Long questionId, Principal principal) {
        User user = userService.getByEmail(principal.getName());
        Question questionFromDb = questionRepository.findById(questionId)
                .orElseThrow(() -> new QuestionNotFoundException(String.format("Question with id=%s not found", questionId)));

        if (!user.getId().equals(questionFromDb.getUserId())) {
            throw new IllegalActionException("You can not delete someone else's question");
        }

        if (!recipeId.equals(questionFromDb.getRecipeId())) {
            throw new QuestionNotFoundException(String.format("Question with id=%s not found for this recipe", questionId));
        }

        questionRepository.deleteById(questionId);
    }

    private Question createQuestionEntity(Long recipeId, QuestionDto questionDto, User user) {
        return Question.builder()
                .userId(user.getId())
                .recipeId(recipeId)
                .question(questionDto.getQuestion())
                .date(LocalDateTime.now())
                .build();
    }
}
