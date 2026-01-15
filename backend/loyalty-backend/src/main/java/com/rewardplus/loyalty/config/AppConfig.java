package com.rewardplus.loyalty.config;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Application-wide configuration class.
 * Configures ModelMapper, CORS, and other application settings.
 */
@Configuration
public class AppConfig {

    /**
     * Configures ModelMapper for entity-to-DTO conversions.
     * ModelMapper automatically maps fields with matching names.
     */
    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();
        
        // Configure strict matching to avoid unexpected mappings
        modelMapper.getConfiguration()
            .setMatchingStrategy(org.modelmapper.convention.MatchingStrategies.STRICT)
            .setAmbiguityIgnored(false)
            .setPreferNestedProperties(false);
            
        return modelMapper;
    }

    /**
     * Configures CORS (Cross-Origin Resource Sharing) for the API.
     * Allows frontend applications to communicate with the backend.
     */
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                    .allowedOrigins("*")
                    .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
                    .allowedHeaders("*")
                    .exposedHeaders("Authorization", "Content-Type")
                    .maxAge(3600);
            }
        };
    }
}

