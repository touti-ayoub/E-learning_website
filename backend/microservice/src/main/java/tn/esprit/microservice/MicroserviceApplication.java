package tn.esprit.microservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class MicroserviceApplication {

	public static void main(String[] args) {
		SpringApplication.run(MicroserviceApplication.class, args);
	}

}
