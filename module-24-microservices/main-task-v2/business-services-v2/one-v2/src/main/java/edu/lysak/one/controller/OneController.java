package edu.lysak.one.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class OneController {

    @GetMapping("/one/message")
    public String test(@RequestHeader("one-request") String header) {
        log.info(header);
        return "Hello from one-service";
    }

}
