package br.com.willpitz.api.controller;

import br.com.willpitz.api.request.CreateVoteRequest;
import br.com.willpitz.api.response.GetAllVotesResponse;
import br.com.willpitz.api.response.GetVoteResponse;
import br.com.willpitz.api.response.PostCreateVoteResponse;
import br.com.willpitz.service.VoteService;
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
@RequestMapping("/v1/vote")
@AllArgsConstructor
@Tag(name = "Votos")
public class VoteController {

    private final VoteService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Cria um novo voto.")
    @ApiResponse(responseCode = "201")
    public Mono<PostCreateVoteResponse> createVote(@Valid @RequestBody CreateVoteRequest request) {
        return service.save(request);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Busca todos os votos.")
    @ApiResponse(responseCode = "200")
    public Flux<GetAllVotesResponse> findAll() {
        return service.findAll();
    }

    @GetMapping(value = "/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Busca um voto.")
    @ApiResponse(responseCode = "200")
    public Mono<GetVoteResponse> getVote(@PathVariable("id") final UUID id) {
        return service.getVote(id);
    }
}
