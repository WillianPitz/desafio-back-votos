package br.com.willpitz.service;

import br.com.willpitz.api.client.CpfValidatorClient;
import br.com.willpitz.api.request.CreateVoteRequest;
import br.com.willpitz.api.response.GetAllVotesResponse;
import br.com.willpitz.api.response.GetVoteResponse;
import br.com.willpitz.api.response.PostCreateVoteResponse;
import br.com.willpitz.domain.enums.SessionStatusEnum;
import br.com.willpitz.exception.BadRequestException;
import br.com.willpitz.exception.NotFoundException;
import br.com.willpitz.persistence.entity.VoteEntity;
import br.com.willpitz.persistence.entity.VotingSessionEntity;
import br.com.willpitz.persistence.repository.VoteRepository;
import br.com.willpitz.persistence.repository.VotingSessionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

import static java.util.Optional.ofNullable;

@Slf4j
@Service
@RequiredArgsConstructor
public class VoteService {

    private final VotingSessionRepository votingSessionRepository;
    private final VoteRepository voteRepository;

    private final CpfValidatorClient client;

    public Mono<PostCreateVoteResponse> save(final CreateVoteRequest request) {
        return votingSessionRepository.findById(request.getVotingSessionId())
            .doOnNext(votingSessionEntity -> log.info("Received data to submit vote with voting session id: {}", votingSessionEntity.getId()))
            .flatMap(votingSessionEntity -> validateIsAbleToVote(request.getCpf(), votingSessionEntity))
            .flatMap(this::validateVotingSession)
            .flatMap(votingSessionEntity -> saveVote(request, votingSessionEntity)
            )
            .map(voteEntity -> PostCreateVoteResponse.builder().id(voteEntity.getId()).build());
    }

    private Mono<VoteEntity> saveVote(final CreateVoteRequest request, final VotingSessionEntity votingSessionEntity) {
        return voteRepository.save(VoteEntity.builder()
                .voteEnum(request.getVote())
                .votingSessionId(votingSessionEntity.getId())
                .cpf(request.getCpf())
                .build())
            .doOnError(DuplicateKeyException.class, ex -> {
                log.error("Error creating vote: {}", request.getVote());
                throw new BadRequestException("Este cpf já possui um voto registrado");
            });
    }

    private Mono<VotingSessionEntity> validateIsAbleToVote(final String cpf, final VotingSessionEntity votingSessionEntity) {
        return client.validateCpf(cpf)
            .onErrorMap(ex -> {
                throw new NotFoundException(ex.getMessage());
            })
            .thenReturn(votingSessionEntity);
    }

    private Mono<VotingSessionEntity> validateVotingSession(final VotingSessionEntity votingSessionEntity) {
        ofNullable(votingSessionEntity)
            .map(VotingSessionEntity::getStatus)
            .filter(SessionStatusEnum.CLOSED::equals)
            .ifPresent(sessionStatusEnum -> { throw new BadRequestException("Essa sessão já foi fechada e não está mais disponível para votos"); });

        return Mono.just(votingSessionEntity);
    }

    public Flux<GetAllVotesResponse> findAll() {
        return voteRepository.findAll()
            .map(voteEntity -> GetAllVotesResponse.builder()
                .id(voteEntity.getId())
                .votingSessionId(voteEntity.getVotingSessionId())
                .cpf(voteEntity.getCpf())
                .voteEnum(voteEntity.getVoteEnum())
                .build())
            .switchIfEmpty(Mono.empty());
    }

    public Mono<GetVoteResponse> getVote(final UUID id) {
        return voteRepository.findById(id)
            .map(voteEntity -> GetVoteResponse.builder()
                .id(voteEntity.getId())
                .votingSessionId(voteEntity.getVotingSessionId())
                .cpf(voteEntity.getCpf())
                .voteEnum(voteEntity.getVoteEnum())
                .build())
            .switchIfEmpty(Mono.error(new NotFoundException("Não foi possível localizar nenhum registro para o id: %s".formatted(id))));
    }
}
