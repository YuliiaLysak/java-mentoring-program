package edu.lysak.recipes.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.List;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "recipes")
public class Recipe {

    @JsonIgnore
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Name should not be blank")
    private String name;

    @NotBlank(message = "Category should not be blank")
    private String category;

    private LocalDateTime date;

    @NotBlank(message = "Description should not be blank")
    private String description;

    @OneToMany(cascade = {CascadeType.ALL})
    @JoinColumn(name="recipe_id", referencedColumnName="id")
    private List<RecipesIngredients> ingredients;

    private String directions;
}
