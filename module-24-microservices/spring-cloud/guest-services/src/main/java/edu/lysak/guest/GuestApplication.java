package edu.lysak.guest;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class GuestApplication {

    public static void main(String[] args) {
        SpringApplication.run(GuestApplication.class, args);
    }
}
