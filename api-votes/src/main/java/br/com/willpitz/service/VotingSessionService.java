package br.com.willpitz.service;

import br.com.willpitz.api.request.CreateVotingSessionRequest;
import br.com.willpitz.api.response.GetAllVotingSessionResponse;
import br.com.willpitz.api.response.GetVotingSessionResponse;
import br.com.willpitz.api.response.PostCreateVotingSessionResponse;
import br.com.willpitz.domain.enums.SessionStatusEnum;
import br.com.willpitz.domain.enums.VoteEnum;
import br.com.willpitz.exception.BadRequestException;
import br.com.willpitz.exception.NotFoundException;
import br.com.willpitz.messaging.VoteResultProducer;
import br.com.willpitz.messaging.messages.VoteResultMessage;
import br.com.willpitz.persistence.entity.PollEntity;
import br.com.willpitz.persistence.entity.VoteEntity;
import br.com.willpitz.persistence.entity.VotingSessionEntity;
import br.com.willpitz.persistence.repository.PollRepository;
import br.com.willpitz.persistence.repository.VoteRepository;
import br.com.willpitz.persistence.repository.VotingSessionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DurationFormatUtils;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class VotingSessionService {

    private final PollRepository pollRepository;
    private final VotingSessionRepository votingSessionRepository;
    private final VoteRepository voteRepository;
    private final TaskScheduler taskScheduler;
    private final VoteResultProducer voteResultProducer;

    public Mono<PostCreateVotingSessionResponse> save(final CreateVotingSessionRequest request) {

        return pollRepository.findById(request.getPollId())
            .doOnNext(pollEntity -> log.info("Received data to open a new voting session with poll id: {}", request.getPollId()))
            .doOnNext(this::validateSession)
            .flatMap(pollEntity -> Mono.zip(saveAndUpdateEntities(request, pollEntity), Mono.just(pollEntity))
            )
            .doOnNext(
                tuple -> taskScheduler.schedule(() -> closeVoteSessionSchedule(tuple.getT1(), tuple.getT2().getId()),
                    Instant.now().plus(request.getDuration())))
            .map(tuple -> PostCreateVotingSessionResponse.builder().id(tuple.getT1().getId()).build());
    }

    private void validateSession(final PollEntity pollEntity) {
        Optional.ofNullable(pollEntity.getVotingSessionId())
            .ifPresent(uuid -> { throw new BadRequestException("Já existe uma sessão criada para esta pauta"); });
    }

    private Mono<VotingSessionEntity> saveAndUpdateEntities(final CreateVotingSessionRequest request, final PollEntity pollEntity) {
        return votingSessionRepository.save(VotingSessionEntity.builder()
                .status(SessionStatusEnum.OPENED)
                .duration(request.getDuration())
                .build())
            .doOnNext(votingSessionEntity -> pollRepository.save(pollEntity.withVotingSessionId(votingSessionEntity.getId()).withUpdatedAt(LocalDateTime.now()))
                .subscribe())
            .doOnError(ex -> {
                log.error("Error creating voting session with poll id: {}", request.getPollId());
                throw new BadRequestException("Ocorreu um erro ao tentar criar uma sessão de votos para a pauta: %s".formatted(pollEntity.getName()));
            });
    }

    private void closeVoteSessionSchedule(final VotingSessionEntity votingSessionEntity, final UUID pollId) {
        log.info("Closing voting session with id: {}", votingSessionEntity.getId());

        pollRepository.findById(pollId)
            .flatMap(pollEntity -> Mono.zip(Mono.just(votingSessionEntity), Mono.just(pollEntity)))
            .doOnNext(tuple -> votingSessionRepository.save(tuple.getT1()
                    .withStatus(SessionStatusEnum.CLOSED)
                    .withClosedDate(LocalDateTime.now()))
                .subscribe())
            .doOnNext(tuple -> voteRepository.findAllByVotingSessionId(tuple.getT1().getId())
                .collectList()
                .flatMap(voteEntities -> updatePollWithVotes(tuple, voteEntities)
                    .thenReturn(voteEntities))
                .doOnSuccess(voteEntities -> sendMessage(votingSessionEntity, voteEntities))
                .subscribe())
            .subscribe();
    }

    private Mono<PollEntity> updatePollWithVotes(final Tuple2<VotingSessionEntity, PollEntity> tuple, final List<VoteEntity> voteEntities) {
        return pollRepository.save(
            tuple.getT2()
                .withTotalVotes(voteEntities.size())
                .withVotesAgainst(getVotes(voteEntities, VoteEnum.NAO))
                .withVotesInFavour(getVotes(voteEntities, VoteEnum.SIM)).withUpdatedAt(LocalDateTime.now()));
    }

    private void sendMessage(final VotingSessionEntity votingSessionEntity, final List<VoteEntity> voteEntities) {
        final var voteResultMessage = VoteResultMessage.builder()
            .votingSessionId(votingSessionEntity.getId())
            .totalVotes(voteEntities.size())
            .votesInFavour(getVotes(voteEntities, VoteEnum.SIM))
            .votesAgainst(getVotes(voteEntities, VoteEnum.NAO))
            .build();

        voteResultProducer.send(voteResultMessage);
    }

    private static int getVotes(final List<VoteEntity> voteEntities, final VoteEnum voteEnum) {
        return voteEntities.stream()
            .filter(voteEntity -> voteEnum.equals(voteEntity.getVoteEnum()))
            .toList().size();
    }

    public Flux<GetAllVotingSessionResponse> findAll() {
        return votingSessionRepository.findAll()
            .map(votingSessionEntity -> GetAllVotingSessionResponse.builder()
                .id(votingSessionEntity.getId())
                .openedDate(votingSessionEntity.getOpenedDate())
                .closedDate(votingSessionEntity.getClosedDate())
                .duration(formatDuration(votingSessionEntity))
                .status(votingSessionEntity.getStatus())
                .build())
            .switchIfEmpty(Mono.empty());
    }

    public Mono<GetVotingSessionResponse> getVotingSession(final UUID id) {
        return votingSessionRepository.findById(id)
            .map(votingSessionEntity -> GetVotingSessionResponse.builder()
                .id(votingSessionEntity.getId())
                .openedDate(votingSessionEntity.getOpenedDate())
                .closedDate(votingSessionEntity.getClosedDate())
                .duration(formatDuration(votingSessionEntity))
                .status(votingSessionEntity.getStatus())
                .build())
            .switchIfEmpty(Mono.error(new NotFoundException("Não foi possível localizar nenhum registro para o id: %s".formatted(id))));
    }

    private static String formatDuration(final VotingSessionEntity votingSessionEntity) {
        return DurationFormatUtils.formatDuration(votingSessionEntity.getDuration().toMillis(), "HH:mm:ss", true);
    }
}
