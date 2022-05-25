package edu.lysak.sport.domain.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import edu.lysak.sport.domain.dto.SportAttributes;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class SportDto {

    private int id;
    private String type;
    private SportAttributes attributes;
}
