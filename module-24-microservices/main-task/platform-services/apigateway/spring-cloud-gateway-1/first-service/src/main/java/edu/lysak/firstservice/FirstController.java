package edu.lysak.firstservice;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FirstController {

    @GetMapping("/employee/message")
    public String test(@RequestHeader("first-request") String header) {
        System.out.println(header);
        return "Hello from First Service";
    }
}
