package tn.esprit.microservice1.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    // CORS is now handled by the API Gateway
    /* 
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("http://localhost:4200")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true);
    }

    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        
        // Allow requests from Angular frontend
        config.addAllowedOrigin("http://localhost:4200");
        
        // Allow all HTTP methods
        config.addAllowedMethod("*");
        
        // Allow all headers
        config.addAllowedHeader("*");
        
        // Allow cookies
        config.setAllowCredentials(true);
        
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }
    */

    @Override
    public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
        configurer
            .defaultContentType(MediaType.APPLICATION_JSON)
            .mediaType("json", MediaType.APPLICATION_JSON);
    }

    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        List<MediaType> supportedMediaTypes = new ArrayList<>();
        supportedMediaTypes.add(MediaType.APPLICATION_JSON);
        supportedMediaTypes.add(new MediaType("application", "json", java.nio.charset.StandardCharsets.UTF_8));
        supportedMediaTypes.add(new MediaType("application", "*+json"));
        converter.setSupportedMediaTypes(supportedMediaTypes);
        converters.add(converter);
    }
    
    @Bean
    public com.fasterxml.jackson.databind.Module jsonViewModule() {
        return new com.fasterxml.jackson.databind.module.SimpleModule()
            .addSerializer(
                tn.esprit.microservice1.entities.Course.class,
                new com.fasterxml.jackson.databind.ser.std.StdSerializer<tn.esprit.microservice1.entities.Course>(tn.esprit.microservice1.entities.Course.class) {
                    @Override
                    public void serialize(
                        tn.esprit.microservice1.entities.Course course,
                        com.fasterxml.jackson.core.JsonGenerator gen,
                        com.fasterxml.jackson.databind.SerializerProvider provider
                    ) throws java.io.IOException {
                        gen.writeStartObject();
                        gen.writeNumberField("id", course.getId());
                        gen.writeStringField("title", course.getTitle());
                        gen.writeStringField("description", course.getDescription());
                        gen.writeNumberField("price", course.getPrice().doubleValue());
                        gen.writeBooleanField("free", course.getFree() != null ? course.getFree() : false);
                        
                        // Write category ID
                        Long categoryId = course.getCategory() != null ? course.getCategory().getId() : null;
                        if (categoryId != null) {
                            gen.writeNumberField("categoryId", categoryId);
                        }
                        
                        // Write cover image data if available
                        if (course.getCoverImageBase64() != null) {
                            gen.writeStringField("coverImageData", course.getCoverImageBase64());
                        }
                        
                        // Write date fields
                        if (course.getCreatedAt() != null) {
                            gen.writeStringField("createdAt", course.getCreatedAt().toString());
                        }
                        if (course.getUpdatedAt() != null) {
                            gen.writeStringField("updatedAt", course.getUpdatedAt().toString());
                        }
                        
                        // Write lessons array if available
                        if (course.getLessons() != null && !course.getLessons().isEmpty()) {
                            gen.writeFieldName("lessons");
                            gen.writeStartArray();
                            for (tn.esprit.microservice1.entities.Lesson lesson : course.getLessons()) {
                                gen.writeObject(lesson);
                            }
                            gen.writeEndArray();
                        }
                        
                        gen.writeEndObject();
                    }
                }
            );
    }
}