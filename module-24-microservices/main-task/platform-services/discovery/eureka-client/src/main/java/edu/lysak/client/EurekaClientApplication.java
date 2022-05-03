package edu.lysak.client;

import com.netflix.discovery.EurekaClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Lazy;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class EurekaClientApplication implements GreetingController {

    private final EurekaClient eurekaClient;
    private final String appName;

    public EurekaClientApplication(
            @Lazy EurekaClient eurekaClient,
            @Value("${spring.application.name}") String appName
    ) {
        this.eurekaClient = eurekaClient;
        this.appName = appName;
    }

    public static void main(String[] args) {
        SpringApplication.run(EurekaClientApplication.class, args);
    }

    @Override
    public String greeting() {
        return String.format(
                "Hello from '%s'!", eurekaClient.getApplication(appName).getName());
    }
}
