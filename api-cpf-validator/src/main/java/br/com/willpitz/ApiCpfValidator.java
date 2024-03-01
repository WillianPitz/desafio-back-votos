package br.com.willpitz;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.reactive.config.EnableWebFlux;


@EnableWebFlux
@SpringBootApplication
public class ApiCpfValidator {

    public static void main(String[] args) {
        SpringApplication.run(ApiCpfValidator.class, args);
    }
}