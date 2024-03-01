package br.com.willpitz.api.controller;

import br.com.willpitz.api.request.CreateVotingSessionRequest;
import br.com.willpitz.api.response.GetAllVotingSessionResponse;
import br.com.willpitz.api.response.GetVotingSessionResponse;
import br.com.willpitz.api.response.PostCreateVotingSessionResponse;
import br.com.willpitz.service.VotingSessionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RestController
@RequestMapping("/v1/voting-session")
@AllArgsConstructor
@Tag(name = "Sessões de votos")
public class VotingSessionController {

    private final VotingSessionService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Cria uma sessão de votos.")
    @ApiResponse(responseCode = "201")
    public Mono<PostCreateVotingSessionResponse> createVotingSession(@Valid @RequestBody CreateVotingSessionRequest request) {
        return service.save(request);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Busca todas as sessões de votos.")
    @ApiResponse(responseCode = "200")
    public Flux<GetAllVotingSessionResponse> findAll() {
        return service.findAll();
    }

    @GetMapping(value = "/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Busca uma sessão de votos")
    @ApiResponse(responseCode = "200")
    public Mono<GetVotingSessionResponse> getVotingSession(@PathVariable("id") final UUID id) {
        return service.getVotingSession(id);
    }
}
