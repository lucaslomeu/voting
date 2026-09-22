package com.lomeu.votingapi.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI votingApiOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Voting API")
                        .version("v1")
                        .description("REST API for topics, voting sessions, votes and voting results."));
    }
}
