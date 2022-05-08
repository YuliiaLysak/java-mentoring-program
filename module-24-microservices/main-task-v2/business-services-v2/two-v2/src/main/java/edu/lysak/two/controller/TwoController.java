package edu.lysak.two.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TwoController {

    @GetMapping("/two/message")
    public String test(@RequestHeader("two-request") String header) {
        log.info(header);
        return "Hello from two-service";
    }

}
