package com.greenleaves.openshift.ipservice;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.net.InetAddress;
import java.net.UnknownHostException;

@SpringBootApplication
public class IpApplication {

	public static void main(String[] args) {
		SpringApplication.run(IpApplication.class, args);
	}

	@Bean
	public RestTemplate restTemplate() {
		return new RestTemplate();
	}
}

@RestController
@Slf4j
class IpController {

	private final
	RestTemplate restTemplate;


	@Autowired
	public IpController(RestTemplate restTemplate) {
		this.restTemplate = restTemplate;
	}

	@GetMapping
	public String get() throws UnknownHostException {
		InetAddress inetAddress = InetAddress.getLocalHost();
		//String greeting = restTemplate.getForObject("http://hello-service:8080/", String.class);
		//log.info(greeting);
		log.info(inetAddress.getHostName());
		log.info(inetAddress.getHostAddress());
		return System.getenv("HOSTNAME") + ", " + inetAddress.getHostName() + ", " + inetAddress.getHostAddress();
	}
}
