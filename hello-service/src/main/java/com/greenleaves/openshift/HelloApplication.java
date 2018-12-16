package com.greenleaves.openshift;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.cluster.CamelClusterEventListener;
import org.apache.camel.cluster.CamelClusterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.net.InetAddress;
import java.net.UnknownHostException;

@Slf4j
@SpringBootApplication
@EnableConfigurationProperties({GreetingProperties.class})
public class HelloApplication {
	public static void main(String[] args) {
		SpringApplication.run(HelloApplication.class, args);
	}

	@Autowired
    ElectionService electionService;

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Scheduled(fixedRate = 1000L)
    public void leaderWrite() throws UnknownHostException {
	    if (electionService.isLeader()) {
            log.info("I am leader {}", InetAddress.getLocalHost().getHostName());
        }
    }



}

@Slf4j
@Component
@Getter
class ElectionService {
    private boolean isLeader;

    @Autowired
    CamelClusterService clusterService;

    @PostConstruct
    public void init() throws Exception {
        clusterService.getView("lock2").addEventListener((CamelClusterEventListener.Leadership) (view, leader) -> {
            // here we get a notification of a change in the leadership
            this.isLeader = leader.isPresent() && leader.get().isLocal();
        });
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