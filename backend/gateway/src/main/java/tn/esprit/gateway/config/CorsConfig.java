package tn.esprit.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
public class CorsConfig {

    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public CorsWebFilter corsWebFilter() {
        CorsConfiguration corsConfig = new CorsConfiguration();

        // Allow credentials (cookies, auth headers)
        corsConfig.setAllowCredentials(true);

        // Allow any port on localhost (for your Angular app)
        corsConfig.setAllowedOriginPatterns(List.of("http://localhost:*"));

        // Allow standard HTTP methods
        corsConfig.setAllowedMethods(List.of(
                "GET", "POST", "PUT", "DELETE", "OPTIONS", "HEAD", "PATCH"
        ));

        // Allow any header from the client
        corsConfig.addAllowedHeader(CorsConfiguration.ALL);

        // Expose headers you need in the browser
        corsConfig.setExposedHeaders(List.of(
                "Authorization",
                "Content-Disposition",
                "Content-Length",
                "Content-Type"
        ));

        // Cache preflight response for 1 hour
        corsConfig.setMaxAge(3600L);

        // Apply this CORS config to all paths
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfig);

        return new CorsWebFilter(source);
    }
}
