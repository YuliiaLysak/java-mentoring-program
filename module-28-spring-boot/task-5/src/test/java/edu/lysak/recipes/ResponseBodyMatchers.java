package edu.lysak.recipes;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import edu.lysak.recipes.controller.ControllerExceptionHandler.*;
import org.springframework.test.web.servlet.ResultMatcher;

import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ResponseBodyMatchers {
    private final ObjectMapper objectMapper = new ObjectMapper();

    {
//        com.fasterxml.jackson.databind.exc.InvalidDefinitionException:
//        Java 8 date/time type `java.time.LocalDateTime` not supported by default:
//        add Module "com.fasterxml.jackson.datatype:jackson-datatype-jsr310" to enable handling
        objectMapper.registerModule(new JavaTimeModule());
    }

    public ResultMatcher containsError(String expectedFieldName, String expectedMessage) {
        return mvcResult -> {
            String json = mvcResult.getResponse().getContentAsString();
            ErrorResult errorResult = objectMapper.readValue(json, ErrorResult.class);
            List<FieldValidationError> fieldErrors = errorResult.getFieldErrors().stream()
                    .filter(fieldError -> fieldError.getField().equals(expectedFieldName))
                    .filter(fieldError -> fieldError.getMessage().equals(expectedMessage))
                    .collect(Collectors.toList());

            assertThat(fieldErrors)
                    .hasSize(1)
                    .withFailMessage("expecting exactly 1 error message"
                                    + "with field name '%s' and message '%s'",
                            expectedFieldName,
                            expectedMessage);
        };
    }

    public <T> ResultMatcher containsObjectAsJson(Object expectedObject, Class<T> targetClass) {
        return mvcResult -> {
            String json = mvcResult.getResponse().getContentAsString();
            T actualObject = objectMapper.readValue(json, targetClass);
            assertEquals(expectedObject, actualObject);
        };
    }

    public static ResponseBodyMatchers responseBody() {
        return new ResponseBodyMatchers();
    }
}
