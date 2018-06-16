package com.greenleaves.openshift.ipservice;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.InetAddress;
import java.net.UnknownHostException;

@SpringBootApplication
public class IpApplication {

	public static void main(String[] args) {
		SpringApplication.run(IpApplication.class, args);
	}
}

@RestController
@Slf4j
class IpController {

	@GetMapping
	public String get() throws UnknownHostException {
		InetAddress inetAddress = InetAddress.getLocalHost();
		log.info(inetAddress.getHostName());
		log.info(inetAddress.getHostAddress());
		return System.getenv("HOSTNAME") + ", " + inetAddress.getHostName() + ", " + inetAddress.getHostAddress();
	}
}
