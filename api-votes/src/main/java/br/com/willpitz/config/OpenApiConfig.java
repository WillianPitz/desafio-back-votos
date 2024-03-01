package br.com.willpitz.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.info.BuildProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Optional;

@Configuration
public class OpenApiConfig {

    @Value("${spring.application.name}")
    private String application;

    @Bean
    public OpenAPI openApiDoc(@Autowired(required = false) BuildProperties buildProperties) {
        var version = Optional.ofNullable(buildProperties)
            .map(p -> String.format("%s-%s", p.getVersion(), p.getTime().getEpochSecond()))
            .orElse(null);

        return new OpenAPI()
            .info(new Info()
                .title(application)
                .description("Api Documentation")
                .version(version)
            );
    }
}
