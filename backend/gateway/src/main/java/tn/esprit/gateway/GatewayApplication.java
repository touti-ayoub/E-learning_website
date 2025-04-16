package tn.esprit.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@EnableDiscoveryClient
public class GatewayApplication {

	public static void main(String[] args) {
		SpringApplication.run(GatewayApplication.class, args);
	}
	@Bean
	public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
		return builder.routes()
				.route("microservice1", r-> r.path("/candidat/**").uri("lb://microservice1"))
<<<<<<< Updated upstream
				.route("microservice5", r-> r.path("/mic5/**").uri("lb://microservice5")).
=======
				.route("microservice2", r-> r.path("/mic2/**").uri("lb://microservice2"))
				.route("user-microservice", r-> r.path("/auth/**").uri("lb://user-microservice"))
				.route("microservice", r-> r.path("/quizzes/**").uri("lb://microservice"))
				.route("microservice5", r-> r.path("/mic5/**").uri("lb://microservice5"))
				.route("microservice3", r-> r.path("/mic3/**").uri("lb://microservice3")).
>>>>>>> Stashed changes

				build();
	}
}
