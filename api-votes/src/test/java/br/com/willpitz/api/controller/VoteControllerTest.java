package br.com.willpitz.api.controller;

import br.com.willpitz.api.request.CreateVoteRequest;
import br.com.willpitz.api.response.GetAllVotesResponse;
import br.com.willpitz.api.response.GetVoteResponse;
import br.com.willpitz.service.VoteService;
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
@WebFluxTest(controllers = VoteController.class)
class VoteControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private VoteService service;

    @Test
    void shouldSaveVote() throws Exception {
        final var contractPath = "/contracts/controller/request/post-save-vote-request.json";

        final var requestJson = jsonFromFile(contractPath);
        final var request = jsonStrToObject(
            requestJson,
            CreateVoteRequest.class
        );

        when(service.save(request))
            .thenReturn(Mono.empty());

        webTestClient.post()
            .uri("/vote")
            .contentType(APPLICATION_JSON)
            .body(BodyInserters.fromValue(request))
            .exchange()
            .expectStatus().isCreated();
    }

    @Test
    void shouldFindAllVotes() throws Exception {
        final var contractPath = "/contracts/controller/response/get-all-votes-response.json";

        final var responseFromJson = jsonToObject(
            contractPath,
            GetAllVotesResponse.class
        );

        when(service.findAll())
            .thenReturn(Flux.just(responseFromJson));

        webTestClient.get()
            .uri("/vote")
            .exchange()
            .expectStatus().isOk()
            .expectBody().jsonPath(contractPath);
    }

    @Test
    void shouldGetVoteById() throws Exception {
        final var contractPath = "/contracts/controller/response/get-vote-by-id-response.json";

        final var responseFromJson = jsonToObject(
            contractPath,
            GetVoteResponse.class
        );

        when(service.getVote(any()))
            .thenReturn(Mono.just(responseFromJson));

        webTestClient.get()
            .uri("/vote/{id}", UUID.randomUUID())
            .exchange()
            .expectStatus().isOk()
            .expectBody().jsonPath(contractPath);
    }
}
