package com.greenleaves.openshift;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
@EnableDiscoveryClient
public class HelloApplication {
	public static void main(String[] args) {
		SpringApplication.run(HelloApplication.class, args);
	}

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}

@RestController
@Slf4j
class HelloController {
    @Value("${greeting.message}")
    private String message;

	@Value("${greeting.test}")
	private String test;

	final
    private DiscoveryClient discoveryClient;

	private final RestTemplate restTemplate;

    @Autowired
    public HelloController(DiscoveryClient discoveryClient, RestTemplate restTemplate) {
        this.discoveryClient = discoveryClient;
        this.restTemplate = restTemplate;
    }

    @GetMapping("/")
	public String get() {
        log.info(test);
        String ip = restTemplate.getForObject("http://ip-service:8080/", String.class);

		return "Hello, " + message + ", you are: " + ip;
	}
}