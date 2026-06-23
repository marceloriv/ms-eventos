package com.ticketti.ms_eventos.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;

@Configuration
public class SwaggerConfig {

    @Value("${server.port:8083}")
	private String serverPort;

    @Bean
    public OpenAPI customOpenAPI() {
        String jwtSchemeName = "bearerAuth";
        SecurityScheme jwtScheme = new SecurityScheme()
                .name(jwtSchemeName)
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT");

        SecurityRequirement securityRequirement = new SecurityRequirement()
                .addList(jwtSchemeName);

        return new OpenAPI()
                .info(new Info()
                        .title("API para eventos de Ticketti")
                        .version("1.0")
                        .description("Documentación de la API de eventos de la empresa Ticketti"))
                .components(new Components().addSecuritySchemes(jwtSchemeName, jwtScheme))
                .addSecurityItem(securityRequirement);
    }
}
