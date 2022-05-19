package edu.lysak.domain.models;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Optional;

@Getter
@AllArgsConstructor
public enum PizzaSize {

    SMALL("small", 5.0),
    MEDIUM("medium", 7.0),
    LARGE("large", 9.0),
    EXTRA_LARGE("extra large", 11.0);

    private final String description;
    private final Double price;

    public static Optional<PizzaSize> getByDescription(String description) {
        PizzaSize[] values = values();
        return Arrays.stream(values)
                .filter(it -> description.equalsIgnoreCase(it.getDescription()))
                .findFirst();
    }
}
