package br.com.willpitz.api.service;

import br.com.willpitz.api.request.CreateVotingSessionRequest;
import br.com.willpitz.api.response.GetAllVotingSessionResponse;
import br.com.willpitz.api.response.GetVotingSessionResponse;
import br.com.willpitz.exception.BadRequestException;
import br.com.willpitz.messaging.VoteResultProducer;
import br.com.willpitz.persistence.entity.PollEntity;
import br.com.willpitz.persistence.entity.VoteEntity;
import br.com.willpitz.persistence.entity.VotingSessionEntity;
import br.com.willpitz.persistence.repository.PollRepository;
import br.com.willpitz.persistence.repository.VoteRepository;
import br.com.willpitz.persistence.repository.VotingSessionRepository;
import br.com.willpitz.service.VotingSessionService;
import org.apache.commons.lang3.time.DurationFormatUtils;
import org.jeasy.random.EasyRandom;
import org.jeasy.random.EasyRandomParameters;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.scheduling.TaskScheduler;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VotingSessionServiceTest {

    private static final EasyRandom GENERATOR = new EasyRandom(new EasyRandomParameters().seed(1L));

    @InjectMocks
    private VotingSessionService service;

    @Mock
    private PollRepository pollRepository;
    @Mock
    private VotingSessionRepository repository;
    @Mock
    private VoteRepository voteRepository;
    @Mock
    private TaskScheduler taskScheduler;
    @Mock
    private VoteResultProducer voteResultProducer;

    @Test
    void shouldSaveVotingSession() throws InterruptedException {
        final var request = GENERATOR.nextObject(CreateVotingSessionRequest.class);
        final var votingSessionEntity = GENERATOR.nextObject(VotingSessionEntity.class);
        final var pollEntity = GENERATOR.nextObject(PollEntity.class);
        final var voteEntity = GENERATOR.nextObject(VoteEntity.class);
        pollEntity.setVotingSessionId(null);
        request.setDuration(Duration.ofSeconds(0));

        when(pollRepository.findById(any(UUID.class))).thenReturn(Mono.just(pollEntity));
        when(repository.save(any())).thenReturn(Mono.just(votingSessionEntity));
        when(pollRepository.save(any())).thenReturn(Mono.just(pollEntity));
        when(voteRepository.findAllByVotingSessionId(any())).thenReturn(Flux.just(voteEntity));

        final var result = service.save(request);

        doAnswer(invocation -> {
            Runnable task = invocation.getArgument(0);

            task.run();
            return null;
        }).when(taskScheduler).schedule(any(Runnable.class), any(Instant.class));

        TimeUnit.SECONDS.sleep(1);

        StepVerifier.create(result)
            .expectNextMatches(response -> response.getId().equals(votingSessionEntity.getId()))
            .expectComplete()
            .verify();

        verify(pollRepository, times(2)).findById(any(UUID.class));
        verify(repository, times(2)).save(any());
        verify(pollRepository, times(2)).save(any());
        verify(taskScheduler, times(1)).schedule(any(Runnable.class), any(Instant.class));
        verify(voteRepository, times(1)).findAllByVotingSessionId(any(UUID.class));
        verify(voteResultProducer, times(1)).send(any());
    }

    @Test
    void shouldReturnExceptionWhenExistsVotingSessionId() {
        final var request = GENERATOR.nextObject(CreateVotingSessionRequest.class);
        final var pollEntity = GENERATOR.nextObject(PollEntity.class);
        request.setDuration(Duration.ofSeconds(0));

        when(pollRepository.findById(any(UUID.class))).thenReturn(Mono.just(pollEntity));

        final var result = service.save(request);

        StepVerifier.create(result)
            .expectError(BadRequestException.class)
            .verify();

        verify(pollRepository, times(1)).findById(any(UUID.class));
    }

    @Test
    void shouldReturnExceptionWhenSaveVotingSession() {
        final var request = GENERATOR.nextObject(CreateVotingSessionRequest.class);
        final var pollEntity = GENERATOR.nextObject(PollEntity.class);
        pollEntity.setVotingSessionId(null);
        request.setDuration(Duration.ofSeconds(0));

        when(pollRepository.findById(any(UUID.class))).thenReturn(Mono.just(pollEntity));
        when(repository.save(any())).thenReturn(Mono.error(RuntimeException::new));

        final var result = service.save(request);

        StepVerifier.create(result)
            .expectError(BadRequestException.class)
            .verify();

        verify(pollRepository, times(1)).findById(any(UUID.class));
        verify(repository, times(1)).save(any());
    }


    @Test
    void shouldFindAllVotingSessions() {
        final var entity = GENERATOR.nextObject(VotingSessionEntity.class);

        when(repository.findAll()).thenReturn(Flux.just(entity));

        final var result = service.findAll();

        final var expected = GetAllVotingSessionResponse.builder()
            .id(entity.getId())
            .status(entity.getStatus())
            .closedDate(entity.getClosedDate())
            .openedDate(entity.getOpenedDate())
            .duration(DurationFormatUtils.formatDuration(entity.getDuration().toMillis(), "HH:mm:ss", true))
            .build();

        verify(repository).findAll();

        StepVerifier.create(result)
            .expectNext(expected)
            .expectComplete()
            .verify();
    }

    @Test
    void shouldFindVotingSessionById() {
        final var entity = GENERATOR.nextObject(VotingSessionEntity.class);

        final var id = UUID.randomUUID();

        when(repository.findById(id)).thenReturn(Mono.just(entity));

        final var result = service.getVotingSession(id);

        final var expected = GetVotingSessionResponse.builder()
            .id(entity.getId())
            .status(entity.getStatus())
            .closedDate(entity.getClosedDate())
            .openedDate(entity.getOpenedDate())
            .duration(DurationFormatUtils.formatDuration(entity.getDuration().toMillis(), "HH:mm:ss", true))
            .build();

        verify(repository).findById(id);

        StepVerifier.create(result)
            .expectNext(expected)
            .expectComplete()
            .verify();
    }
}
