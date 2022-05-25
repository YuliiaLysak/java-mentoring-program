package edu.lysak.sport.domain.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class SportAttributes {

    private String name;
    private String description;
    private String slug;
    private String icon;

}
