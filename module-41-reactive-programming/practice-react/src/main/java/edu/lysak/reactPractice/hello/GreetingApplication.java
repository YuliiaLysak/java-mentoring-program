package edu.lysak.reactPractice.hello;

import edu.lysak.reactPractice.hello.client.GreetingClient;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class GreetingApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(GreetingApplication.class, args);
        GreetingClient greetingClient = context.getBean(GreetingClient.class);
        // We need to block for the content here or the JVM might exit before the message is logged
        System.out.println(">> message = " + greetingClient.getMessage().block());
    }
}
