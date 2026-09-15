package com.sunrise.dental.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI sunriseDentalOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Sunrise Dental Clinic API")
                        .description("Distributed RESTful Web Services for Appointment Scheduling, Patient Records, and Billing Management.")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Sunrise Dental Clinic Engineering Team")
                                .email("contact@sunrisedental.lk")
                                .url("https://sunrisedental.lk"))
                        .license(new License()
                                .name("Educational University Project License")));
    }
}
