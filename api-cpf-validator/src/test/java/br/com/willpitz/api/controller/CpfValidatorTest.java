package br.com.willpitz.api.controller;

import br.com.willpitz.service.CpfValidatorService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@WebFluxTest(controllers = CpfValidatorController.class)
class CpfValidatorTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private CpfValidatorService service;

    @Test
    void shouldValidateCpf() throws Exception {
        when(service.validateCpf(any())).thenReturn(Mono.empty());

        webTestClient.get().uri(uriBuilder -> uriBuilder.path("/validate").queryParam("cpf", UUID.randomUUID()).build())
            .exchange()
            .expectStatus()
            .isOk();
    }
}
