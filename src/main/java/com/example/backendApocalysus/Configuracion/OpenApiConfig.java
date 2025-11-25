package com.example.backendApocalysus.Configuracion;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.Components;


@Configuration
public class OpenApiConfig {
    @Bean
    public OpenAPI apocalysusOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("API Club Deportivo Apocalysus")
                        .description("API REST para la gestión y tienda virtual del club deportivo Apocalysus. " +
                                "Incluye módulos de autenticación, productos, carrito, pedidos y administración.")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Santiago Calderón Almario")
                                .email("tu-email@usco.edu.co")
                                .url("https://github.com/tu-usuario"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0.html")))
                .addSecurityItem(new SecurityRequirement().addList("Bearer Authentication"))
                .components(new Components()
                        .addSecuritySchemes("Bearer Authentication",
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .description("Ingresa el token JWT obtenido del endpoint /api/auth/login")));
    }
}
