package com.example.finmonitor.config;

import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiGroupConfig {

    @Bean
    public GroupedOpenApi authApi() {
        return GroupedOpenApi.builder()
                .group("1. Auth")
                .pathsToMatch("/auth/**")
                .build();
    }

    @Bean
    public GroupedOpenApi StatusApi() {
        return GroupedOpenApi.builder()
                .group("2. Transaction Attributes")
                .pathsToMatch("/status/**", "/banks/**", "/transaction-types/**", "/categories/**")
                .build();
    }


    @Bean
    public GroupedOpenApi transactionApi() {
        return GroupedOpenApi.builder()
                .group("3. Transactions")
                .pathsToMatch("/transactions/**")
                .build();
    }

    @Bean
    public GroupedOpenApi dashboardApi() {
        return GroupedOpenApi.builder()
                .group("4. Dashboard")
                .pathsToMatch("/dashboard/**")
                .build();
    }

    @Bean
    public GroupedOpenApi exportApi() {
        return GroupedOpenApi.builder()
                .group("5. Export")
                .pathsToMatch("/export/**")
                .build();
    }
}
