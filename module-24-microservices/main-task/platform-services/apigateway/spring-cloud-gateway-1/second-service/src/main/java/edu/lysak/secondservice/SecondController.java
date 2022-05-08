package edu.lysak.secondservice;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class SecondController {

    @GetMapping("/consumer/message")
    public String test(@RequestHeader("second-request") String header) {
        log.info(header);
        return "Hello from Second Service";
    }

}
