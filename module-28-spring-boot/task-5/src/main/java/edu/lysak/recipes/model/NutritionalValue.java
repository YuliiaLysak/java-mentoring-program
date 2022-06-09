package edu.lysak.recipes.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.validation.constraints.NotNull;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
@Embeddable
public class NutritionalValue {

    @NotNull
    @Column(name = "calories")
    private Integer calories;

    @NotNull
    @Column(name = "protein")
    private Integer protein;

    @NotNull
    @Column(name = "fat")
    private Integer fat;

    @NotNull
    @Column(name = "carbohydrate")
    private Integer carbohydrate;

}
