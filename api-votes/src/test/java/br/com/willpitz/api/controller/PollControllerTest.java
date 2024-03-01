package br.com.willpitz.api.controller;

import br.com.willpitz.api.request.PollRequest;
import br.com.willpitz.api.response.GetAllPollsResponse;
import br.com.willpitz.api.response.GetPollResponse;
import br.com.willpitz.service.PollService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

import static br.com.willpitz.api.utils.TestUtil.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;

@ExtendWith(SpringExtension.class)
@WebFluxTest(controllers = PollController.class)
class PollControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private PollService service;

    @Test
    void shouldSavePoll() throws Exception {
        final var contractPath = "/contracts/controller/request/post-save-poll-request.json";

        final var requestJson = jsonFromFile(contractPath);
        final var request = jsonStrToObject(
            requestJson,
            PollRequest.class
        );

        when(service.save(request))
            .thenReturn(Mono.empty());

        webTestClient.post()
            .uri("/poll")
            .contentType(APPLICATION_JSON)
            .body(BodyInserters.fromValue(request))
            .exchange()
            .expectStatus().isCreated();
    }

    @Test
    void shouldFindAllPolls() throws Exception {
        final var contractPath = "/contracts/controller/response/get-all-polls-response.json";

        final var responseFromJson = jsonToObject(
            contractPath,
            GetAllPollsResponse.class
        );

        when(service.findAll())
            .thenReturn(Flux.just(responseFromJson));

        webTestClient.get()
            .uri("/poll")
            .exchange()
            .expectStatus().isOk()
            .expectBody().jsonPath(contractPath);
    }

    @Test
    void shouldGetPollById() throws Exception {
        final var contractPath = "/contracts/controller/response/get-poll-by-id-response.json";

        final var responseFromJson = jsonToObject(
            contractPath,
            GetPollResponse.class
        );

        when(service.getPoll(any()))
            .thenReturn(Mono.just(responseFromJson));

        webTestClient.get()
            .uri("/poll/{id}", UUID.randomUUID())
            .exchange()
            .expectStatus().isOk()
            .expectBody().jsonPath(contractPath);
    }
}
