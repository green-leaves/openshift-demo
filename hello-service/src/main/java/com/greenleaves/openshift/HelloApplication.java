package com.greenleaves.openshift;

import io.fabric8.kubernetes.api.model.ConfigMap;
import io.fabric8.kubernetes.api.model.ObjectMeta;
import io.fabric8.kubernetes.client.KubernetesClient;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.cluster.CamelClusterEventListener;
import org.apache.camel.cluster.CamelClusterService;
import org.apache.camel.component.kubernetes.KubernetesConfiguration;
import org.apache.camel.component.kubernetes.KubernetesHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import javax.annotation.PostConstruct;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@SpringBootApplication
@EnableSwagger2
@EnableConfigurationProperties({GreetingProperties.class})
public class HelloApplication implements CommandLineRunner {
	public static void main(String[] args) {
		SpringApplication.run(HelloApplication.class, args);
	}

	@Autowired
    ElectionService electionService;

    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.any())
                .paths(PathSelectors.any())
                .build();
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public KubernetesClient kubernetesClient() {
        return KubernetesHelper.getKubernetesClient(new KubernetesConfiguration());
    }

    @Scheduled(fixedRate = 1000L)
    public void leaderWrite() throws UnknownHostException {
	    if (electionService.isLeader()) {
            log.info("I am leader {}", InetAddress.getLocalHost().getHostName());
        }
    }


    @Override
    public void run(String... args) throws Exception {
        log.info("Master HOST =====> {}", kubernetesClient().getMasterUrl().getHost());
        ConfigMap configMap = new ConfigMap();
        HashMap<String, String> hashMap = new HashMap<>();
        ObjectMeta metadata = new ObjectMeta();
        metadata.setName("flexi");
        configMap.setMetadata(metadata);
        hashMap.put("pricing.rate-source", "manual");
        configMap.setData(hashMap);
        kubernetesClient().configMaps().createOrReplace(configMap);
        kubernetesClient().configMaps().list().getItems().forEach(item -> log.info(item.toString()));
        kubernetesClient().configMaps().withName("flexi").get().getData().forEach((key, value) -> log.info(key + ": " + value));

    }
}

@Slf4j
@RestController
@RequestMapping("/configmaps")
class ConfigMapsController {

    final
    KubernetesClient kubernetesClient;

    @Autowired
    public ConfigMapsController(KubernetesClient kubernetesClient) {
        this.kubernetesClient = kubernetesClient;
    }

    @GetMapping("/{name}/{key}")
    public String getConfigByConfigMapNameAndKey(@PathVariable("name") String name, @PathVariable("key") String key) {
        return kubernetesClient.configMaps().withName(name).get().getData().get(key);
    }

    @PutMapping("/{name}/{key}/{value}")
    public String updateConfigByConfigMapNameAndKey(@PathVariable("name") String name,
                                                    @PathVariable("key") String key,
                                                    @PathVariable("value") String value) {
        ConfigMap configMap = kubernetesClient.configMaps().withName(name).get();
        configMap.getData().put(key, value);
        kubernetesClient.configMaps().createOrReplace(configMap);
        return kubernetesClient.configMaps().withName(name).get().getData().get(key);
    }

    @PostMapping("/{name}")
    public Map<String, String> createConfigMap(@PathVariable("name") String name, @RequestBody Map<String, String> map) {
        ConfigMap configMap = new ConfigMap();
        ObjectMeta metadata = new ObjectMeta();
        metadata.setName(name);
        configMap.setData(map);
        configMap.setMetadata(metadata);
        kubernetesClient.configMaps().create(configMap);

        return  kubernetesClient.configMaps().withName(name).get().getData();
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