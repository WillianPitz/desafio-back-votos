package br.com.willpitz.service;

import br.com.willpitz.api.request.PollRequest;
import br.com.willpitz.api.response.GetAllPollsResponse;
import br.com.willpitz.api.response.GetPollResponse;
import br.com.willpitz.api.response.PostCreatePollResponse;
import br.com.willpitz.exception.BadRequestException;
import br.com.willpitz.exception.NotFoundException;
import br.com.willpitz.persistence.entity.PollEntity;
import br.com.willpitz.persistence.repository.PollRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PollService {

    private final PollRepository repository;

    public Mono<PostCreatePollResponse> save(final PollRequest pollRequest) {
        log.info("Received data to create poll with name: {}", pollRequest.getName());

        return repository.save(PollEntity.builder()
                .name(pollRequest.getName())
                .description(pollRequest.getDescription())
                .build())
            .doOnError(ex -> {
                log.error("Error creating poll: {}", pollRequest.getName());
                throw new BadRequestException("Ocorreu um erro ao tentar salvar a pauta");
            })
            .map(pollEntity -> PostCreatePollResponse.builder().id(pollEntity.getId()).build());
    }

    public Flux<GetAllPollsResponse> findAll() {
        return repository.findAll()
            .map(pollEntity -> GetAllPollsResponse.builder()
                .id(pollEntity.getId())
                .name(pollEntity.getName())
                .description(pollEntity.getDescription())
                .createdAt(pollEntity.getCreatedAt())
                .updatedAt(pollEntity.getUpdatedAt())
                .totalVotes(pollEntity.getTotalVotes())
                .votesAgainst(pollEntity.getVotesAgainst())
                .votesInFavour(pollEntity.getVotesInFavour())
                .build())
            .switchIfEmpty(Mono.empty());
    }

    public Mono<GetPollResponse> getPoll(final UUID id) {
        return repository.findById(id)
            .map(pollEntity -> GetPollResponse.builder()
                .id(pollEntity.getId())
                .name(pollEntity.getName())
                .description(pollEntity.getDescription())
                .createdAt(pollEntity.getCreatedAt())
                .updatedAt(pollEntity.getUpdatedAt())
                .totalVotes(pollEntity.getTotalVotes())
                .votesAgainst(pollEntity.getVotesAgainst())
                .votesInFavour(pollEntity.getVotesInFavour())
                .build())
            .switchIfEmpty(Mono.error(new NotFoundException("Não foi possível localizar nenhum registro para o id: %s".formatted(id))));
    }
}
