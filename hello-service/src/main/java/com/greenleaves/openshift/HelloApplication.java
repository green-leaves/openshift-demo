package com.greenleaves.openshift;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
public class HelloApplication {
	public static void main(String[] args) {
		SpringApplication.run(HelloApplication.class, args);
	}
}

@RestController
@Slf4j
class HelloController {
    @Value("${greeting.message}")
    private String message;

	@Value("${greeting.test}")
	private String test;

	@GetMapping("/")
	public String get() {
        log.info(test);
		return "Hello, " + message;
	}
}