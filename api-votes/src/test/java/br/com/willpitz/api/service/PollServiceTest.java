package br.com.willpitz.api.service;

import br.com.willpitz.api.request.PollRequest;
import br.com.willpitz.api.response.GetAllPollsResponse;
import br.com.willpitz.api.response.GetPollResponse;
import br.com.willpitz.exception.BadRequestException;
import br.com.willpitz.persistence.entity.PollEntity;
import br.com.willpitz.persistence.repository.PollRepository;
import br.com.willpitz.service.PollService;
import org.jeasy.random.EasyRandom;
import org.jeasy.random.EasyRandomParameters;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PollServiceTest {

    private static final EasyRandom GENERATOR = new EasyRandom(new EasyRandomParameters().seed(1L));

    @InjectMocks
    private PollService service;

    @Mock
    private PollRepository repository;

    @Test
    void shouldSavePoll() {
        final var request = GENERATOR.nextObject(PollRequest.class);
        final var entity = GENERATOR.nextObject(PollEntity.class);

        when(repository.save(any())).thenReturn(Mono.just(entity));

        final var result = service.save(request);

        verify(repository).save(any());

        StepVerifier.create(result)
            .expectNextMatches(response -> response.getId().equals(entity.getId()))
            .expectComplete()
            .verify();
    }

    @Test
    void shouldReturnExceptionOnSavePoll() {
        final var request = GENERATOR.nextObject(PollRequest.class);

        when(repository.save(any())).thenReturn(Mono.error(RuntimeException::new));

        final var result = service.save(request);

        StepVerifier.create(result)
            .expectError(BadRequestException.class)
            .verify();

        verify(repository, times(1)).save(any());
    }

    @Test
    void shouldFindAllPolls() {
        final var entity = GENERATOR.nextObject(PollEntity.class);

        when(repository.findAll()).thenReturn(Flux.just(entity));

        final var result = service.findAll();

        final var expected = GetAllPollsResponse.builder()
            .id(entity.getId())
            .votesInFavour(entity.getVotesInFavour())
            .votesAgainst(entity.getVotesAgainst())
            .totalVotes(entity.getTotalVotes())
            .createdAt(entity.getCreatedAt())
            .updatedAt(entity.getUpdatedAt())
            .description(entity.getDescription())
            .name(entity.getName())
            .build();

        verify(repository).findAll();

        StepVerifier.create(result)
            .expectNext(expected)
            .expectComplete()
            .verify();
    }

    @Test
    void shouldFindPollById() {
        final var entity = GENERATOR.nextObject(PollEntity.class);

        final var id = UUID.randomUUID();

        when(repository.findById(id)).thenReturn(Mono.just(entity));

        final var result = service.getPoll(id);

        final var expected = GetPollResponse.builder()
            .id(entity.getId())
            .votesInFavour(entity.getVotesInFavour())
            .votesAgainst(entity.getVotesAgainst())
            .totalVotes(entity.getTotalVotes())
            .createdAt(entity.getCreatedAt())
            .updatedAt(entity.getUpdatedAt())
            .description(entity.getDescription())
            .name(entity.getName())
            .build();

        verify(repository).findById(id);

        StepVerifier.create(result)
            .expectNext(expected)
            .expectComplete()
            .verify();
    }
}
