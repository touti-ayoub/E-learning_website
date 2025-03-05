package tn.esprit.microservice1.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // Allow all endpoints
                .allowedOrigins("http://localhost:4200") // Allow your frontend origin
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // Allow specific HTTP methods
                .allowCredentials(true); // Allow credentials if needed
    }
}