package edu.lysak.reactPractice.hello.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Greeting {

    @Id
    private Long id;
    private String message;

    public Greeting(String message) {
        this.message = message;
    }
}
