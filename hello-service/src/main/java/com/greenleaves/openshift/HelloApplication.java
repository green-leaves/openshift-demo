package com.greenleaves.openshift;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
@EnableConfigurationProperties({GreetingProperties.class})
public class HelloApplication {
	public static void main(String[] args) {
		SpringApplication.run(HelloApplication.class, args);
	}

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}

@Slf4j
@RestController
class HelloController {

    final
    GreetingProperties greeting;

	private final RestTemplate restTemplate;

    @Autowired
    public HelloController(RestTemplate restTemplate, GreetingProperties greetingProperties) {
        this.restTemplate = restTemplate;
        this.greeting = greetingProperties;
    }

    @GetMapping("/")
	public String get() {
        log.info(greeting.getTest());
        log.info(greeting.getInfo());
        //String ip = restTemplate.getForObject("http://ip-service:8080/", String.class);
		//return "Hello, " + message + ", you are: " + ip;
		return "Hello, " + greeting.getMessage();
	}
}
@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "greeting")
class GreetingProperties {
    private String message;
    private String test;
    private String info;
}