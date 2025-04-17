package tn.esprit.microservice5.Rest.Config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        // Disable CSRF for simplicity (useful when testing with Postman, etc.)
        http.csrf(csrf -> csrf.disable())

                // Authorize all requests to any endpoint
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().permitAll()
                );

        // That’s it! No OAuth2 login, no basic auth, no restrictions.

        return http.build();
    }
}
