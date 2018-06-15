package com.greenleaves.openshift;

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
class HelloController {
    @Value("${greeting.message}")
    private String message;

	@Value("${greeting.test}")
	private String test;

	@GetMapping("/")
	public String get() {
		System.out.println(test);
		return "Hello, " + message;
	}
}
