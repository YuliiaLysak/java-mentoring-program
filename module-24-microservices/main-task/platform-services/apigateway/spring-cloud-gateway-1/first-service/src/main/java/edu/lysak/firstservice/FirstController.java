package edu.lysak.firstservice;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class FirstController {

    @GetMapping("/employee/message")
    public String test(@RequestHeader("first-request") String header) {
        log.info(header);
        return "Hello from First Service";
    }
}
