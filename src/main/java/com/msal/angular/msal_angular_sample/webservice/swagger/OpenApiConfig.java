package com.msal.angular.msal_angular_sample.webservice.swagger;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.*;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.util.List;


@Configuration
public class OpenApiConfig {

    @Primary
    @Bean
    public OpenAPI customOpenAPI(){

        // Replace with your actual Azure Tenant ID and Client ID / Scope
        String tenantId = "aafea113-8a60-476f-97e1-f518d6cd5db0";
        String authUrl = "https://login.microsoftonline.com/" + tenantId + "/oauth2/v2.0/authorize";
        String tokenUrl = "https://login.microsoftonline.com/" + tenantId + "/oauth2/v2.0/token";

        return new OpenAPI()
                .info(new Info().title("GScience Security Microservice")
                        .version("2.0")
                        .description("""
                            API documentation for the Spring Boot security application.
                            Specifications:
                            - Java version: Java 25
                            - Identity Provider: Microsoft Entra ID
                            """)
                        .contact(new Contact()
                                .name("Marc Gerrits")
                                .email("gerrits.marc@hotmail.com")
                        )
                )
                .addSecurityItem(new SecurityRequirement()
                        .addList("Azure_Entra_ID")
                        .addList("Bearer_Token") // Added Bearer Token security requirement
                )
                .components(new Components()
                        // Documenting Microsoft Entra ID OAuth2 / OIDC flow for interactive Swagger UI login
                        .addSecuritySchemes("Azure_Entra_ID", new SecurityScheme()
                                .type(SecurityScheme.Type.OAUTH2)
                                .description("Authenticate using Microsoft Entra ID interactive flow.")
                                .flows(new OAuthFlows()
                                        .authorizationCode(new OAuthFlow()
                                                .authorizationUrl(authUrl)
                                                .tokenUrl(tokenUrl)
                                                .scopes(new Scopes()
                                                        .addString("api://3b2948af-005e-461a-bcfe-3024f8239c32/access_as_user", "Access API as application")
                                                )
                                        )
                                ))

                        // Documenting raw Bearer Token input (pasting token manually from MSAL Angular / Postman)
                        .addSecuritySchemes("Bearer_Token", new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("Paste your raw Microsoft Entra ID JWT Bearer Token here."))
                )
                .servers(List.of(
                        new io.swagger.v3.oas.models.servers.Server()
                                .url("https://msal-angular-sample-webservice.onrender.com")
                                .description("Production Server (Render)"),

                        new io.swagger.v3.oas.models.servers.Server()
                                .url("http://localhost:8080")
                                .description("Local Development Server")
                ));
    }

    @Bean
    public GroupedOpenApi publicApi() {
        OpenApiCustomizer additionalPropsFalse = openApi -> {
            Components components = openApi.getComponents();
            if (components != null && components.getSchemas() != null) {
                // Stream directly from the map values to avoid variable assignment errors
                components.getSchemas().values().forEach(schema -> {
                    if (schema != null && schema.getAdditionalProperties() == null) {
                        schema.setAdditionalProperties(false);
                    }
                });
            }
        };

        return GroupedOpenApi.builder()
                .group("public")
                .pathsToMatch("/**")
                .addOpenApiCustomizer(additionalPropsFalse)
                .build();
    }


}
