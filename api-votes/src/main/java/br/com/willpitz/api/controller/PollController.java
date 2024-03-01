package br.com.willpitz.api.controller;


import br.com.willpitz.api.request.PollRequest;
import br.com.willpitz.api.response.GetAllPollsResponse;
import br.com.willpitz.api.response.GetPollResponse;
import br.com.willpitz.api.response.PostCreatePollResponse;
import br.com.willpitz.service.PollService;
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
@RequestMapping("/v1/poll")
@AllArgsConstructor
@Tag(name = "Pauta")
public class PollController {

    private final PollService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Cria uma nova pauta.")
    @ApiResponse(responseCode = "201")
    public Mono<PostCreatePollResponse> createPoll(@Valid @RequestBody PollRequest pollRequest) {
        return service.save(pollRequest);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Busca todas as pautas.")
    @ApiResponse(responseCode = "200")
    public Flux<GetAllPollsResponse> findAll() {
        return service.findAll();
    }

    @GetMapping(value = "/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Busca uma pauta")
    @ApiResponse(responseCode = "200")
    public Mono<GetPollResponse> getPoll(@PathVariable("id") final UUID id) {
        return service.getPoll(id);
    }
}
