package edu.lysak.recipes.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "recipes")
public class Recipe {

    @JsonIgnore
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotBlank(message = "Name should not be blank")
    private String name;

    @NotBlank(message = "Category should not be blank")
    private String category;

    private LocalDateTime date;

    @NotBlank(message = "Description should not be blank")
    private String description;

    @NotEmpty
    @Size(min = 1, message = "Recipe should contain at least one ingredient")
    @ElementCollection
    private List<String> ingredients;

    @NotEmpty
    @Size(min = 1, message = "Recipe should contain at least one direction")
    @ElementCollection
    private List<String> directions;
}
