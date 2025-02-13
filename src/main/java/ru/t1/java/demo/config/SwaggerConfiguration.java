package ru.t1.java.demo.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;

public class SwaggerConfiguration {
    @Bean
    public OpenAPI openApiConfig() {
        return new OpenAPI()
                .info(new Info()
                        .title("T1 School")
                        .version("v1")
                        .description("T1 School API")
                ).addServersItem(new Server()
                        .url("http://localhost:8080")
                        .description("localhost server"));
    }
}
