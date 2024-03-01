package br.com.willpitz.api.service;

import br.com.willpitz.api.client.CpfValidatorClient;
import br.com.willpitz.api.request.CreateVoteRequest;
import br.com.willpitz.api.response.GetAllVotesResponse;
import br.com.willpitz.api.response.GetVoteResponse;
import br.com.willpitz.domain.enums.SessionStatusEnum;
import br.com.willpitz.exception.BadRequestException;
import br.com.willpitz.exception.NotFoundException;
import br.com.willpitz.persistence.entity.VoteEntity;
import br.com.willpitz.persistence.entity.VotingSessionEntity;
import br.com.willpitz.persistence.repository.VoteRepository;
import br.com.willpitz.persistence.repository.VotingSessionRepository;
import br.com.willpitz.service.VoteService;
import org.jeasy.random.EasyRandom;
import org.jeasy.random.EasyRandomParameters;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DuplicateKeyException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VoteServiceTest {

    private static final EasyRandom GENERATOR = new EasyRandom(new EasyRandomParameters().seed(1L));

    @InjectMocks
    private VoteService service;
    @Mock
    private VotingSessionRepository votingSessionRepository;
    @Mock
    private VoteRepository repository;
    @Mock
    private CpfValidatorClient client;

    @Test
    void shouldSaveVote() {
        final var request = GENERATOR.nextObject(CreateVoteRequest.class);
        final var votingSessionEntity = GENERATOR.nextObject(VotingSessionEntity.class);
        final var voteEntity = GENERATOR.nextObject(VoteEntity.class);
        votingSessionEntity.setStatus(SessionStatusEnum.OPENED);

        when(votingSessionRepository.findById(request.getVotingSessionId())).thenReturn(Mono.just(votingSessionEntity));
        when(client.validateCpf(any())).thenReturn(Mono.just(Boolean.TRUE));
        when(repository.save(any())).thenReturn(Mono.just(voteEntity));

        final var result = service.save(request);

        StepVerifier.create(result)
            .expectNextMatches(response -> response.getId().equals(voteEntity.getId()))
            .expectComplete()
            .verify();

        verify(repository).save(any());
    }

    @Test
    void shouldReturnExceptionOnSaveVote() {
        final var request = GENERATOR.nextObject(CreateVoteRequest.class);
        final var votingSessionEntity = GENERATOR.nextObject(VotingSessionEntity.class);
        votingSessionEntity.setStatus(SessionStatusEnum.OPENED);

        when(votingSessionRepository.findById(request.getVotingSessionId())).thenReturn(Mono.just(votingSessionEntity));
        when(client.validateCpf(any())).thenReturn(Mono.just(Boolean.TRUE));
        when(repository.save(any())).thenReturn(Mono.error(() -> new DuplicateKeyException("")));

        final var result = service.save(request);

        StepVerifier.create(result)
            .expectError(BadRequestException.class)
            .verify();

        verify(repository).save(any());
    }

    @Test
    void shouldReturnExceptionOnValidateCpf() {
        final var request = GENERATOR.nextObject(CreateVoteRequest.class);
        final var votingSessionEntity = GENERATOR.nextObject(VotingSessionEntity.class);
        votingSessionEntity.setStatus(SessionStatusEnum.OPENED);

        when(votingSessionRepository.findById(request.getVotingSessionId())).thenReturn(Mono.just(votingSessionEntity));
        when(client.validateCpf(any())).thenReturn(Mono.error(new RuntimeException()));

        final var result = service.save(request);

        StepVerifier.create(result)
            .expectError(NotFoundException.class)
            .verify();

        verify(repository, never()).save(any());
    }

    @Test
    void shouldReturnExceptionWhenClosed() {
        final var request = GENERATOR.nextObject(CreateVoteRequest.class);
        final var votingSessionEntity = GENERATOR.nextObject(VotingSessionEntity.class);
        votingSessionEntity.setStatus(SessionStatusEnum.CLOSED);

        when(votingSessionRepository.findById(request.getVotingSessionId())).thenReturn(Mono.just(votingSessionEntity));
        when(client.validateCpf(any())).thenReturn(Mono.just(Boolean.TRUE));

        final var result = service.save(request);

        StepVerifier.create(result)
            .expectError(BadRequestException.class)
            .verify();

        verify(repository, never()).save(any());
    }

    @Test
    void shouldFindAllVotes() {
        final var entity = GENERATOR.nextObject(VoteEntity.class);

        when(repository.findAll()).thenReturn(Flux.just(entity));

        final var result = service.findAll();

        final var expected = GetAllVotesResponse.builder()
            .id(entity.getId())
            .voteEnum(entity.getVoteEnum())
            .cpf(entity.getCpf())
            .votingSessionId(entity.getVotingSessionId())
            .build();

        verify(repository).findAll();

        StepVerifier.create(result)
            .expectNext(expected)
            .expectComplete()
            .verify();
    }

    @Test
    void shouldFindVoteById() {
        final var entity = GENERATOR.nextObject(VoteEntity.class);

        final var id = UUID.randomUUID();

        when(repository.findById(id)).thenReturn(Mono.just(entity));

        final var result = service.getVote(id);

        final var expected = GetVoteResponse.builder()
            .id(entity.getId())
            .voteEnum(entity.getVoteEnum())
            .votingSessionId(entity.getVotingSessionId())
            .cpf(entity.getCpf())
            .build();

        verify(repository).findById(id);

        StepVerifier.create(result)
            .expectNext(expected)
            .expectComplete()
            .verify();
    }
}
