package edu.lysak.secondservice;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SecondController {

    @GetMapping("/consumer/message")
    public String test(@RequestHeader("second-request") String header) {
        System.out.println(header);
        return "Hello from Second Service";
    }

}
