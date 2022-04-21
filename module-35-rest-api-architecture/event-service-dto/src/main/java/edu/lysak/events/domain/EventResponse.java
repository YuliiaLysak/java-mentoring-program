package edu.lysak.events.domain;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class EventResponse {

    @Schema(description = "Title of the event", example = "One way or another", required = true)
    private String title;

    @Schema(description = "Place of the event", example = "City, Country", required = true)
    private String place;

    @Schema(description = "Speaker of the event", example = "John Doe", required = true)
    private String speaker;

    @Schema(description = "Type of the event", example = "concert", required = true)
    private String eventType;

    @Schema(description = "Date and time of the event", example = "2022-06-01T19:00:00", required = true)
    private LocalDateTime dateTime;
}
