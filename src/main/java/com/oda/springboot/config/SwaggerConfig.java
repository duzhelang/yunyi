package com.oda.springboot.config;

import org.springframework.context.annotation.Configuration;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;

@Configuration
@OpenAPIDefinition(
    info = @Info(
        title = "Spring Boot中使用SpringDoc构建RESTful APIs",
        version = "1.0"
    )
)
public class SwaggerConfig {
}

