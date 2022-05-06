package edu.lysak.one.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class OneController {

    @GetMapping("/one/message")
    public String test(@RequestHeader("one-request") String header) {
        System.out.println(header);
        return "Hello from one-service";
    }

}
