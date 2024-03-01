package br.com.willpitz.api.controller;

import br.com.willpitz.api.request.CreateVotingSessionRequest;
import br.com.willpitz.api.response.GetAllVotingSessionResponse;
import br.com.willpitz.api.response.GetVotingSessionResponse;
import br.com.willpitz.service.VotingSessionService;
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
@WebFluxTest(controllers = VotingSessionController.class)
class VotingSessionControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private VotingSessionService service;

    @Test
    void shouldSaveVotingSession() throws Exception {
        final var contractPath = "/contracts/controller/request/post-save-voting-session-request.json";

        final var requestJson = jsonFromFile(contractPath);
        final var request = jsonStrToObject(
            requestJson,
            CreateVotingSessionRequest.class
        );

        when(service.save(request))
            .thenReturn(Mono.empty());

        webTestClient.post()
            .uri("/voting-session")
            .contentType(APPLICATION_JSON)
            .body(BodyInserters.fromValue(request))
            .exchange()
            .expectStatus().isCreated();
    }

    @Test
    void shouldFindAllVotingSessions() throws Exception {
        final var contractPath = "/contracts/controller/response/get-all-voting-sessions-response.json";

        final var responseFromJson = jsonToObject(
            contractPath,
            GetAllVotingSessionResponse.class
        );

        when(service.findAll())
            .thenReturn(Flux.just(responseFromJson));

        webTestClient.get()
            .uri("/voting-session")
            .exchange()
            .expectStatus().isOk()
            .expectBody().jsonPath(contractPath);
    }

    @Test
    void shouldGetVotingSessionById() throws Exception {
        final var contractPath = "/contracts/controller/response/get-voting-session-by-id-response.json";

        final var responseFromJson = jsonToObject(
            contractPath,
            GetVotingSessionResponse.class
        );

        when(service.getVotingSession(any()))
            .thenReturn(Mono.just(responseFromJson));

        webTestClient.get()
            .uri("/voting-session/{id}", UUID.randomUUID())
            .exchange()
            .expectStatus().isOk()
            .expectBody().jsonPath(contractPath);
    }
}
