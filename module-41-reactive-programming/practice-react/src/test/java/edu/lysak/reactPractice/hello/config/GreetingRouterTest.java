package edu.lysak.reactPractice.hello.config;

import edu.lysak.reactPractice.hello.domain.Greeting;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.assertj.core.api.Assertions.assertThat;

/*
    As of Spring Boot 2.1, we no longer need @ExtendWith() to load the SpringExtension
    because it's included as a meta annotation in the Spring Boot test
    annotations like @DataJpaTest, @WebMvcTest, and @SpringBootTest.
*/
//@ExtendWith(SpringExtension.class)

//  We create a `@SpringBootTest`, starting an actual server on a `RANDOM_PORT`
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class GreetingRouterTest {

    // Spring Boot will create a `WebTestClient` for you,
    // already configure and ready to issue requests against "localhost:RANDOM_PORT"
    @Autowired
    private WebTestClient webTestClient;

    @Test
    public void testHello() {
        webTestClient
                // Create a GET request to test an endpoint
                .get()
                .uri("/hello-simple")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                // and use the dedicated DSL to test assertions against the response
                .expectStatus().isOk()
                .expectBody(Greeting.class)
                .value(greeting -> assertThat(greeting.getMessage()).isEqualTo("Hello, Spring!"));
    }
}
