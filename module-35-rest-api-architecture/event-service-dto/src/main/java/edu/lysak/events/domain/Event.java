package edu.lysak.events.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "events")
public class Event {

    @JsonIgnore
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Schema(description = "Title of the event", example = "One way or another", required = true)
    @NotBlank(message = "Title should not be blank")
    private String title;

    @Schema(description = "Place of the event", example = "City, Country", required = true)
    @NotBlank(message = "Place should not be blank")
    private String place;

    @Schema(description = "Speaker of the event", example = "John Doe", required = true)
    @NotBlank(message = "Speaker should not be blank")
    private String speaker;

    @Schema(description = "Type of the event", example = "concert", required = true)
    @NotBlank(message = "Type should not be blank")
    @Column(name = "event_type")
    private String eventType;

    @Schema(description = "Date and time of the event", example = "2022-06-01T19:00:00", required = true)
    @Column(name = "date_time")
    private LocalDateTime dateTime;
}
