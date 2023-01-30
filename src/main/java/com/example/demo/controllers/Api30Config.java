package com.example.demo.controllers;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(//описывает название проекта в svagger ui
        info = @Info(title = "DEMO_JAVA", version = "v0.1"))
public class Api30Config {
}
