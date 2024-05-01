package com.example.javapractice.multirequest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class HelloController {
    private final HelloService helloService;

    @GetMapping("/test")
    public String hello() {
        log.info("controller start!");
        String json = helloService.run();
        log.info("controller End!");
        return json;
    }
}
